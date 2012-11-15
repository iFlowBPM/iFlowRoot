Notas:
Estas paginas foram feitas com JSTL (JSP Tag Library) como teste preliminar as suas
verdadeiras potencialidades.

Para o efeito foram criadas 3 tags novas:
<if:message string="nome.string"/> - Vai buscar a string externalizada "nome.string"

<if:getFileList var="file_list" type="file_type"/> - coloca na variavel "file_list" a lista
                                                      de ficheiros do repositorio do tipo 
                                                      "file_type"

<if:checkUserAccess /> - Verifica se o utilizador tem permissao para aceder a esta pagina.
                          Caso possa aceder, a pagina eh apresentada. Caso contrario devolve
                          erro e termina o processamento da pagina. (TODO)

