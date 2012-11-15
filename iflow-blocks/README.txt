Compilar e mover as classes do projecto para ${iflow.home}/repository_data/1/Classes


Adicionar Novos campos ao Formulario.
    1 - JSP<Denominação do novo Campo>Data
        - Localização: "pt.iknow.floweditor.blocks"
        - Propriedades que serão apresentadas no editor, usadas para configuração do campo

    2 - pt.iflow.blocks.form.<Denominação do novo Campo>
        - Geração de xml e valores importantes a serem usados quando o fluxo esta a gerar o formulario

    3 - Classe JSPFieldTypeEnum
        - Adicionar entrada na classe JSPFieldTypeEnum, campos por ordem:
            - code -> inteiro que também permitará a ordenação dos campos na combobox do dialogo adicionar campo
            - descrKey -> Nome do campo definida no ficheiro "editor_blocks.properties" (Apresentada na combobox do novo campo)
            - tooltipKey -> tooltip do campo definida no ficheiro "editor_blocks.properties" (Apresentada na combobox do novo campo)
            - editorClass -> Nome da classe com as definições das propriedades do novo campo (criada no ponto 1)
            - engineClass -> classe com caminho que é responsavel pela construção do campo (xml) (criada no ponto 2)
            - extraButtons -> Botões adicionar e remover linha (nas propriedades multiplas)

    4 - JSPFieldData.java
        - Localização dos "tipos" de campos, que podem ser usados nas propriedades do ponto 1
        - Definição de combobox é realizada aqui.

        - _hsDisableDataTypes - listagem de tipos de dados que nao podem ser usados na variavel
