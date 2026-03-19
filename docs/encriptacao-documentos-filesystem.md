# Encriptacao de Documentos no Filesystem - Guia de Implementacao

## 1. Contexto

Quando a propriedade `DOCS_BASE_URL` esta configurada no ficheiro `iflow-home/config/iflow.properties`, os documentos sao guardados no filesystem em vez da base de dados. Na base de dados fica apenas o URL/path do ficheiro. O objectivo e encriptar os binarios guardados no filesystem para proteger a informacao em repouso.

```properties
# iflow-home/config/iflow.properties
# Colocar esta propriedade se se pretender guardar os documentos no filesystem
DOCS_BASE_URL=F:\\infosistema\\documents
```

---

## 2. Arquitectura Actual de Armazenamento

### 2.1 Configuracao (Const.java)

```java
// iflow-api/src/main/java/pt/iflow/api/utils/Const.java (linhas 122-123)
public static String DOCS_BASE_URL = null;
public static String DOCS_DAO_CLASS = null;

// Carregamento (linhas 721-722)
DOCS_BASE_URL = Setup.getProperty("DOCS_BASE_URL");
DOCS_DAO_CLASS = Setup.getProperty("DOCS_DAO_CLASS");
```

### 2.2 Inicializacao do DocumentsBean

O `DocumentsBean` e o unico gateway para operacoes de documentos no filesystem. Na inicializacao, valida o directorio e cria-o se necessario:

```java
// iflow-web/src/main/java/pt/iflow/core/DocumentsBean.java (linhas 80-112)
static String docsBaseUrl = null;
static boolean docDataInDB = true; // false quando usa filesystem

DocumentsBean() {
    docsBaseUrl = Const.DOCS_BASE_URL;
    if (StringUtilities.isNotEmpty(docsBaseUrl)) {
        docDataInDB = false;
        File f = new File(docsBaseUrl);
        if (!f.isDirectory()) {
            docsBaseUrl = FilenameUtils.concat(Const.sIFLOW_HOME, Const.DOCS_BASE_URL);
            f = new File(docsBaseUrl);
            if (!f.isDirectory()) {
                try {
                    FileUtils.forceMkdir(f);
                } catch (Exception e) {
                    try {
                        docsBaseUrl = Const.DOCS_BASE_URL;
                        f = new File(docsBaseUrl);
                        FileUtils.forceMkdir(f);
                    } catch (Exception ex) {
                        Logger.error("", "DocumentsBean", "static",
                            "O URL : '" + Const.DOCS_BASE_URL + "' nao corresponde a uma pasta.");
                        docDataInDB = true;
                        docsBaseUrl = null;
                    }
                }
            }
        }
    }
}
```

### 2.3 Estrutura de Directorios

Os ficheiros sao organizados numa arvore hierarquica de 5 niveis baseada no DocID:

```java
// DocumentsBean.java (linhas 1058-1077)
private String getDocumentFilePath(int docID, String fileName) {
    String strDocIdUrl = "0000000000" + docID;
    strDocIdUrl = strDocIdUrl.substring(strDocIdUrl.length()-10, strDocIdUrl.length());
    String docIdUrl = strDocIdUrl.substring(0, 2) + "\\" + strDocIdUrl.substring(2, 4) + "\\" +
                      strDocIdUrl.substring(4, 6) + "\\" + strDocIdUrl.substring(6, 8) + "\\" +
                      strDocIdUrl.substring(8, 10);
    if (!docDataInDB) {
        String url = FilenameUtils.concat(docsBaseUrl, docIdUrl);
        try {
            File f = new File(url);
            if (!f.isDirectory()) FileUtils.forceMkdir(f);
            Path dir = Paths.get(url);
            Path path = dir.resolve(fileName);
            return path.toAbsolutePath().toString();
        } catch (Exception e) {}
    }
    return null;
}
```

Exemplo para DocID 12345: `F:\infosistema\documents\00\00\01\23\45\ficheiro.pdf`

---

## 3. Pontos de Escrita e Leitura (onde implementar encriptacao)

Existem exactamente **4 pontos** no `DocumentsBean` onde os bytes sao escritos/lidos do filesystem:

### 3.1 ESCRITA - addDocument() (linhas 355-375)

```java
// Adicionar novo documento ao filesystem
if (!docDataInDB && ((filePath = getDocumentFilePath(adoc.getDocId(),
    adoc.getFileName())) != null)) {
    try {
        if (pst != null) DatabaseInterface.safeClose(pst);

        query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT_DOCURL");
        pst = db.prepareStatement(query, generatedKeyNames);
        pst.setString(1, filePath);
        pst.setInt(2, adoc.getDocId());
        pst.executeUpdate();

        Logger.debug(userInfo.getUtilizador(), this, "addDocument",
            "new FileOutputStream:" + filePath);
        OutputStream fos = Files.newOutputStream(
            Paths.get(filePath),
            StandardOpenOption.CREATE_NEW);
        fos.write(adoc.getContent());        // <-- ENCRIPTAR AQUI
        fos.close();
    } catch(FileNotFoundException ex) {
        Logger.error(userInfo.getUtilizador(), this, "addDocument",
            procData.getSignature() + " File not Found.", ex);
    } catch(IOException ioe) {
        Logger.error(userInfo.getUtilizador(), this, "addDocument",
            procData.getSignature() + " IOException.", ioe);
    }
}
```

### 3.2 ESCRITA - updateDocument() (linhas 447-472)

```java
// Actualizar documento existente no filesystem
String filePath = null;
if (docDataInDB || ((filePath = getDocumentFilePath(adoc.getDocId(),
    adoc.getFileName())) == null)) {
    ByteArrayInputStream isBody = new ByteArrayInputStream(adoc.getContent());
    pst.setBinaryStream(++pos, isBody, adoc.getContent().length);
    pst.setString(++pos, null);
} else {
    String folderPath = getDocumentFilePath(adoc.getDocId(), "");
    File f = new File(folderPath);
    File[] fs = f.listFiles();
    for (int i=0; fs!=null && i<fs.length; i++) fs[i].delete();

    pst.setBinaryStream(++pos, null, 0);
    pst.setString(++pos, filePath);
    FileOutputStream fos = null;
    try {
        fos = new FileOutputStream(filePath);
        fos.write(adoc.getContent());        // <-- ENCRIPTAR AQUI
        fos.close();
    } catch(FileNotFoundException ex) {
        Logger.error(userInfo.getUtilizador(), this, "addDocument",
            procData.getSignature() + " File not Found.", ex);
    } catch(IOException ioe) {
        Logger.error(userInfo.getUtilizador(), this, "addDocument",
            procData.getSignature() + " IOException.", ioe);
    } finally {
        if(fos != null) Utils.safeClose(fos);
    }
}
```

### 3.3 ESCRITA - migrateDatabaseToFilesystem() (linhas 1190-1213)

```java
// Migrar documentos da base de dados para o filesystem
Document dbDoc = getDocumentData(userInfo, procData, dbDoc, db, true);
String filePath = getDocumentFilePath(dbDoc.getDocId(), dbDoc.getFileName());
OutputStream fos = Files.newOutputStream(Paths.get(filePath));
fos.write(dbDoc.getContent());               // <-- ENCRIPTAR AQUI
fos.close();

String query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT_DOCURL");
pst = db.prepareStatement(query);
pst.setString(1, filePath);
pst.setInt(2, docid);
pst.executeUpdate();
```

### 3.4 LEITURA - getDocumentData() (linhas 711-756)

```java
// Ler documento do filesystem
String filePath = rs.getString("docurl");
if (StringUtils.isNotEmpty(filePath)) {
    retObj.setDocurl(filePath);
    File f = new File(filePath);
    length = (int)f.length();
}
// ...
if (abFull) {
    if (StringUtils.isNotEmpty(filePath)) {
        try (InputStream dataStream = new FileInputStream(filePath);) {
            if (null != dataStream) {
                byte[] r = new byte[STREAM_SIZE];
                int j = 0;
                while ((j = dataStream.read(r, 0, STREAM_SIZE)) != -1)
                    baos.write(r, 0, j);
                dataStream.close();
            }
        }
    } else {
        // Leitura da base de dados (sem alteracao necessaria)
        try (InputStream dataStream = rs.getBinaryStream("datadoc");) {
            if (null != dataStream) {
                byte[] r = new byte[STREAM_SIZE];
                int j = 0;
                while ((j = dataStream.read(r, 0, STREAM_SIZE)) != -1)
                    baos.write(r, 0, j);
                dataStream.close();
            }
        }
    }

    baos.flush();
    baos.close();
    retObj.setContent(baos.toByteArray());    // <-- DESENCRIPTAR AQUI
}
```

**NOTA sobre o length:** Quando os ficheiros estao encriptados, o `length` obtido com `f.length()` corresponde ao tamanho encriptado, nao ao tamanho real. Considerar se isto e relevante para a aplicacao. Se for, guardar o tamanho original na base de dados ou calcular apos desencriptacao.

---

## 4. Mecanismos de Encriptacao Existentes no Projeto

### 4.1 CryptUtils interno (DocumentHash.java)

```java
// iflow-api/src/main/java/pt/iflow/api/documents/DocumentHash.java (linhas 171-228)
private static class CryptUtils {
    private Cipher _encryptor;
    private Cipher _decryptor;

    public CryptUtils() {
        byte[] keyBytes = new byte[16];
        byte[] b = "segredo".getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

        _encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");

        _encryptor.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        _decryptor.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    }

    public String encrypt(String text) {
        byte[] results = this._encryptor.doFinal(text.getBytes("UTF-8"));
        return new String(UrlBase64.encode(results));
    }

    public String decrypt(String text) {
        byte[] results = this._decryptor.doFinal(UrlBase64.decode(text));
        return new String(results, "UTF-8");
    }
}
```

- **Algoritmo:** AES/CBC/PKCS5Padding
- **Chave:** "segredo" (hardcoded, 16 bytes)
- **IV:** Igual a chave
- **Encoding:** UrlBase64
- **Limitacao:** Trabalha com Strings, nao com byte[] directamente. E uma classe interna privada.

### 4.2 CryptUtils da biblioteca crypto-api (RECOMENDADO)

```java
// Dependencia: crypto-api-1.0.3.jar
// Pacote: pt.iknow.utils.crypt.CryptUtils

// Uso em iflow-api/src/main/java/pt/iflow/api/utils/Utils.java (linhas 44, 50)
import pt.iknow.utils.crypt.CryptUtils;
private static final CryptUtils _crypt = new CryptUtils("achave");
```

Esta biblioteca ja e usada no projecto e aceita uma chave como parametro do construtor. E a opcao mais pratica para reutilizar.

### 4.3 DMSEncryptedCredential

```java
// iflow-connectors/dms-connector/.../DMSEncryptedCredential.java
private static final CryptUtils crypt;
static {
    crypt = new CryptUtils(String.valueOf(new DMSEncryptedCredential().hashCode()));
}

protected DMSEncryptedCredential(String username, byte[] password) {
    this.username = username;
    this.password = crypt.encrypt(new String(password));
}

@Override
public byte[] getPassword() {
    return crypt.decrypt(password).getBytes();
}
```

### 4.4 FileCipher Interface (Applet)

```java
// iflow-applet/src/main/java/pt/iflow/applet/cipher/
public interface FileCipher extends FileAppletService, DynamicFormProvider {
    IVFile encrypt(final IVFile file) throws CipherException;
    IVFile decrypt(final IVFile file) throws CipherException;
}

public enum CipherType {
    NONE(NoneCipherImpl.class),
    RSA(NoneCipherImpl.class),
    PSK(NoneCipherImpl.class),
    PBE(NoneCipherImpl.class);
}
```

**NOTA:** Todas as implementacoes apontam para `NoneCipherImpl` (passthrough/sem encriptacao). Este framework existe mas nao esta activo.

---

## 5. Implementacao Recomendada

### 5.1 Novas propriedades no iflow.properties

```properties
# Encriptacao de documentos no filesystem
DOCS_ENCRYPTION_ENABLED=true
DOCS_ENCRYPTION_KEY=chave-secreta-do-projecto
```

### 5.2 Carregar propriedades no Const.java

```java
// Adicionar em Const.java
public static boolean DOCS_ENCRYPTION_ENABLED = false;
public static String DOCS_ENCRYPTION_KEY = null;

// No bloco de carregamento:
String encEnabled = Setup.getProperty("DOCS_ENCRYPTION_ENABLED");
DOCS_ENCRYPTION_ENABLED = "true".equalsIgnoreCase(encEnabled);
DOCS_ENCRYPTION_KEY = Setup.getProperty("DOCS_ENCRYPTION_KEY");
```

### 5.3 Alteracoes no DocumentsBean.java

#### Adicionar instancia do CryptUtils

```java
import pt.iknow.utils.crypt.CryptUtils;

// No topo da classe, junto aos outros campos estaticos:
private static CryptUtils docCrypt = null;

// Na inicializacao (construtor ou bloco estatico), apos validar docsBaseUrl:
if (Const.DOCS_ENCRYPTION_ENABLED && StringUtilities.isNotEmpty(Const.DOCS_ENCRYPTION_KEY)) {
    docCrypt = new CryptUtils(Const.DOCS_ENCRYPTION_KEY);
    Logger.info("", "DocumentsBean", "init", "Document filesystem encryption ENABLED.");
} else {
    Logger.info("", "DocumentsBean", "init", "Document filesystem encryption DISABLED.");
}
```

#### Metodos auxiliares de encriptacao/desencriptacao de bytes

```java
/**
 * Encripta bytes para escrita no filesystem.
 * Se a encriptacao nao estiver activa, devolve os bytes originais.
 */
private byte[] encryptContent(byte[] content) {
    if (docCrypt == null || content == null || content.length == 0) {
        return content;
    }
    try {
        String encoded = Base64.getEncoder().encodeToString(content);
        String encrypted = docCrypt.encrypt(encoded);
        return encrypted.getBytes(StandardCharsets.UTF_8);
    } catch (Exception e) {
        Logger.error("", "DocumentsBean", "encryptContent",
            "Erro ao encriptar conteudo. A guardar sem encriptacao.", e);
        return content;
    }
}

/**
 * Desencripta bytes lidos do filesystem.
 * Se a encriptacao nao estiver activa, devolve os bytes originais.
 * Inclui fallback para ficheiros nao encriptados (migracao gradual).
 */
private byte[] decryptContent(byte[] content) {
    if (docCrypt == null || content == null || content.length == 0) {
        return content;
    }
    try {
        String encrypted = new String(content, StandardCharsets.UTF_8);
        String decrypted = docCrypt.decrypt(encrypted);
        return Base64.getDecoder().decode(decrypted);
    } catch (Exception e) {
        // Fallback: ficheiro provavelmente nao esta encriptado (pre-migracao)
        Logger.warning("", "DocumentsBean", "decryptContent",
            "Falha ao desencriptar. A assumir conteudo nao encriptado (ficheiro antigo).");
        return content;
    }
}
```

**NOTA:** Se o `CryptUtils` da biblioteca `crypto-api` ja suportar `byte[]` directamente (metodos como `encryptBytes`/`decryptBytes`), usar esses em vez da conversao Base64. Verificar a API da biblioteca antes de implementar.

**Alternativa sem Base64 (se a biblioteca suportar bytes):**

```java
private byte[] encryptContent(byte[] content) {
    if (docCrypt == null || content == null || content.length == 0) {
        return content;
    }
    try {
        return docCrypt.encryptBytes(content);
    } catch (Exception e) {
        Logger.error("", "DocumentsBean", "encryptContent",
            "Erro ao encriptar conteudo.", e);
        return content;
    }
}

private byte[] decryptContent(byte[] content) {
    if (docCrypt == null || content == null || content.length == 0) {
        return content;
    }
    try {
        return docCrypt.decryptBytes(content);
    } catch (Exception e) {
        Logger.warning("", "DocumentsBean", "decryptContent",
            "Falha ao desencriptar. A assumir conteudo nao encriptado.");
        return content;
    }
}
```

### 5.4 Aplicar nos 4 pontos

#### Ponto 1 - addDocument() (linha ~370)

```java
// ANTES:
fos.write(adoc.getContent());

// DEPOIS:
fos.write(encryptContent(adoc.getContent()));
```

#### Ponto 2 - updateDocument() (linha ~465)

```java
// ANTES:
fos.write(adoc.getContent());

// DEPOIS:
fos.write(encryptContent(adoc.getContent()));
```

#### Ponto 3 - migrateDatabaseToFilesystem() (linha ~1200)

```java
// ANTES:
fos.write(dbDoc.getContent());

// DEPOIS:
fos.write(encryptContent(dbDoc.getContent()));
```

#### Ponto 4 - getDocumentData() (linha ~750)

```java
// ANTES:
retObj.setContent(baos.toByteArray());

// DEPOIS:
if (StringUtils.isNotEmpty(filePath)) {
    retObj.setContent(decryptContent(baos.toByteArray()));
} else {
    retObj.setContent(baos.toByteArray()); // dados da BD, sem desencriptar
}
```

**ATENCAO:** A condicao `StringUtils.isNotEmpty(filePath)` garante que so desencripta dados lidos do filesystem, nunca dados vindos da base de dados.

---

## 6. Consideracoes Importantes

### 6.1 Migracao de Documentos Existentes

Documentos ja existentes no filesystem nao estao encriptados. O metodo `decryptContent()` inclui um fallback (try/catch) que detecta ficheiros nao encriptados e devolve o conteudo original. Isto permite uma migracao gradual:

- Ficheiros antigos: lidos sem desencriptacao (fallback)
- Ficheiros novos/actualizados: escritos encriptados
- Quando um ficheiro antigo e actualizado via `updateDocument()`, passa a ficar encriptado

Para forcar a encriptacao de todos os ficheiros existentes, criar um processo batch:

```java
public void encryptExistingFiles() {
    // 1. Listar todos os documentos com docurl preenchido na BD
    // 2. Para cada um:
    //    a. Ler o ficheiro do filesystem
    //    b. Tentar desencriptar - se falhar, e porque nao esta encriptado
    //    c. Se nao esta encriptado, encriptar e reescrever
}
```

### 6.2 Tamanho dos Ficheiros (length)

O `length` reportado por `f.length()` no `getDocumentData()` sera o tamanho encriptado, nao o original. Se isto causar problemas:

- **Opcao A:** Guardar o tamanho original numa coluna da BD
- **Opcao B:** Calcular o tamanho apos desencriptacao (ja acontece implicitamente quando `retObj.setContent()` e chamado)
- **Opcao C:** Ignorar se o length so e usado para alocacao de buffers (nao e critico)

### 6.3 Seguranca da Chave

- Nao fazer commit da chave no repositorio
- Usar uma chave diferente por ambiente (dev, staging, producao)
- A chave deve ter pelo menos 16 caracteres
- Considerar usar variavel de ambiente em vez de ficheiro properties:
  ```java
  String key = System.getenv("DOCS_ENCRYPTION_KEY");
  if (key == null) key = Setup.getProperty("DOCS_ENCRYPTION_KEY");
  ```

### 6.4 Performance

- AES e rapido e tem suporte por hardware (AES-NI) na maioria dos CPUs modernos
- O overhead e minimo para ficheiros de tamanho tipico de documentos
- Se necessario, a encriptacao pode ser feita em streaming (CipherInputStream/CipherOutputStream) para ficheiros muito grandes, em vez de carregar tudo em memoria

### 6.5 Backup e Recuperacao

- Os backups do filesystem conterao ficheiros encriptados
- Para restaurar, e necessario ter a mesma chave de encriptacao
- **Documentar a chave num local seguro e separado dos backups**
- Se a chave for perdida, os documentos no filesystem ficam irrecuperaveis (os que estavam na BD antes da migracao podem ser restaurados do backup da BD)

---

## 7. Ficheiros Relevantes (Referencia Rapida)

| Ficheiro | Caminho | Funcao |
|----------|---------|--------|
| iflow.properties | `iflow-home/config/iflow.properties` | Configuracao (DOCS_BASE_URL, novas props de encriptacao) |
| Const.java | `iflow-api/src/main/java/pt/iflow/api/utils/Const.java` | Constantes e carregamento de propriedades |
| Setup.java | `iflow-api/src/main/java/pt/iflow/api/utils/Setup.java` | Leitura do ficheiro de propriedades |
| DocumentsBean.java | `iflow-web/src/main/java/pt/iflow/core/DocumentsBean.java` | Gateway unico para operacoes de documentos - **ficheiro principal a alterar** |
| DocumentHash.java | `iflow-api/src/main/java/pt/iflow/api/documents/DocumentHash.java` | CryptUtils interno (referencia) |
| Utils.java | `iflow-api/src/main/java/pt/iflow/api/utils/Utils.java` | Uso do CryptUtils da biblioteca crypto-api |
| crypto-api-1.0.3.jar | Dependencia Maven/lib | Biblioteca de encriptacao a reutilizar |
| DMSEncryptedCredential.java | `iflow-connectors/dms-connector/.../DMSEncryptedCredential.java` | Exemplo de uso do CryptUtils |

---

## 8. Checklist de Implementacao

- [ ] Verificar API do `CryptUtils` (crypto-api-1.0.3.jar) - confirmar se suporta `byte[]` ou so `String`
- [ ] Adicionar propriedades `DOCS_ENCRYPTION_ENABLED` e `DOCS_ENCRYPTION_KEY` ao `iflow.properties`
- [ ] Adicionar campos correspondentes ao `Const.java` e carrega-los
- [ ] Adicionar `docCrypt` e metodos `encryptContent()`/`decryptContent()` ao `DocumentsBean`
- [ ] Alterar `addDocument()` - encriptar antes de escrever
- [ ] Alterar `updateDocument()` - encriptar antes de escrever
- [ ] Alterar `migrateDatabaseToFilesystem()` - encriptar antes de escrever
- [ ] Alterar `getDocumentData()` - desencriptar apos leitura do filesystem
- [ ] Testar com documentos novos (criar, ler, actualizar)
- [ ] Testar fallback com documentos antigos nao encriptados
- [ ] Testar migracao DB -> filesystem com encriptacao activa
- [ ] Validar que documentos na BD (sem DOCS_BASE_URL) continuam a funcionar normalmente
- [ ] Configurar chave diferente por ambiente
