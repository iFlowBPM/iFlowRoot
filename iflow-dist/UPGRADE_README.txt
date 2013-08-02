------------------------------------------------------------------------------
                           iFlow Upgrade How To                               

V4.2.0 - [R20120618] - V4.2.0 - [R20130802]
 - Para definir uma língua de defeito para o iFlow adicionar DEFAULT_LOCALE=en-US ao iflow.properties.

V4.2.0 - [R20120423] - V4.2.0 - [R20120618]
 - Para se poder usar o serviço tmn SMS express é necessario Criar/editar as propriedades do iflow
	 #tmn sms express configuration
	 SMS_EXPRESS_USE=true
	 SMS_EXPRESS_USERNAME=<Username do serviço tmn sms express>
	 SMS_EXPRESS_APPLICATION=<nome dado a aplicação (Se nenhum meter string vazia)>
	 SMS_EXPRESS_PASSWORD=<password do serviço tmn sms express>
	 SMS_EXPRESS_ORGANIZATION_SENDER=<Remetente válido ("Originadores") do serviço tmn sms express>
 - Para se poder fazer autenticação AD e obter a informação dos users do AD é preciso
     #Ligar ou não o manager que Sincroniza os utilizadores
     USERSYNC_ON=true
     #Tempo de intervalo (em minutos) em que corre o Manager que sincroniza os utilizadores
     USERSYNC=200
     #OrgId dos utilizadores a Sincronizar
     USERSYNC_ORGID=2
     #UnitId dos utilizadores a Sincronizar
     USERSYNC_UNITID=3

V4.0.16 - V4.2.0 - [R20120423]
 - É necessario actualizar os ficheiros xsl
     - No campo "arraytable" [<xsl:if test="type='arraytable'">] será necessario incluir as propriedades:
       'word-break: break-all;' e 'word-wrap: break-word;'
         - código:
                <xsl:variable name="tdString"><xsl:value-of select="value"></xsl:value-of></xsl:variable>
                <xsl:if test="(not (contains(value,' ')) and string-length(value) > 25 )">
                    <xsl:if test="(not (contains($tdString, 'form.jsp')))">
                        <xsl:attribute name="style">
                            <xsl:text>word-break: break-all;</xsl:text>
                            <xsl:text>word-wrap: break-word;</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                </xsl:if>

V4.0.16 - V4.0.16 - [R20120419]
 - É necessario actualizar o ficheiro "iFlow_iFlowRemote.wsdl" contido em "...\iFlowHome\repository_data\1\WSDL"
 - É necessario correr o script "upgrade_4.0.16_to_4.0.16corrections"
 - É necessario actulizar o ficheiro "task_list.vm"
     - icon na folder 
         - o código "<td title='$act.get("task_annotation_color_title")' ... #if($has_appname)" passou a:
         <td title='$act.get("task_annotation_color_title")'  style="background-color:$act.get("task_annotation_color_backgroundColor")" ></td>
           #if($hasFolder > 0)
            <td onclick="javascript:menuonoff('atribuir$row')" title="$action_move">
            <img class="toolTipImg" src="AnnotationIconsServlet?icon_name='action.png'&ts='$iconTime' border="0">
               <div id="atribuir$row" style="display:none;position:absolute;z-index:1;background:none repeat scroll 0 0 #FFFFFF;border-color:#888888;border-style:solid;border-width:1px 1px 2px;text-align:left;padding:2px 5px"><table>
                #foreach($folder in $folders)
                  <tr><td style="background:$folder.getColor();width:4px;"></td>
                  <td><input type="button" class="apt_regular_button" style="width:15em;text-align:left;margin:0px" value="$folder.getName()" id="$folder.getFolderid()" name="$folder.getName()" onclick="assignActivity('$folder.getFolderid()','$act.get("flowid")_$act.get("pid")_$act.get("subpid")')"></td></tr>
                  #end
                </tr></table><input class="apt_regular_button" type="button" value="$action_close" id="bt_close2" name="$action_close" style="width:6em;text-align:center" onclick=""></div></td>
            #else
                <td></td>
            #end
            #set($row=$row+1)
            #if($has_appname)
 - É necessario actualizar o editor.

V4.0.15 -> V4.0.16
 - Assinar jar da Applet
 - Copiar ficheiros da licença
 - Remover war V4.0.15 do tomcat (webapps e work)
 - Instalar war v4.0.16 no tomcat
 - Executar o script em .\db\upgrade_4.0.15_to_4.0.16.sql
 - Alterações no iflow-home:
 	- Substituir conteúdo da pasta \iflow-home\repository_data\1\Icons\
 	- Substituir conteúdo da pasta \iflow-home\repository_data\1\Messages\
 	- Substituir no ficheiro \iflow-home\repository_data\1\main.vm
	 		- Fazer include do processAnnotations
	 					<script type="text/javascript" src="javascript/processAnnotations.js"></script>
	 		- Acrescentar na div  <div id="div_proc_menu_colapsed"
	 		  			<span id ="process_annotations_span_colapsed"></span>
	 		- Acrescentar na div <div id="div_proc_menu_expanded"
	 					<span id ="process_annotations_span_expanded"></span>
 					
 	- Substituir no ficheiro \iflow-home\repository_data\1\task_list.vm
	 		- Acrescentar depois de <h1 id="title_tasks">$title</h1>
						<div class="yui-skin-sam">
						<div id ="view_proc_annotation_dashboard" ></div>
						</div>
			- Acrescentar depois de <p>$tasksMostRecentMsg</p>
								<p>$label_label
							    <select name="labelpainel" id="labelpainel" onchange="javascript:tabber(2, '$response.encodeURL($url_prefix,'actividades_filtro.jsp')','','$response.encodeURL($url_prefix,'actividades.jsp')','ts=$ts&clean=true&filterlabel='+document.getElementById('labelpainel').value);" >
							    <option value="0">$label_default</option> 
							      #foreach($label in $labels)
							         <option value="$label.getId()"> 
							         	$label.getName()
							         </option> 
								  #end
							     </select>
							    
							   	$days_label
							    <select name="dayspainel" id="dayspainel" onchange="javascript:tabber(2, '$response.encodeURL($url_prefix,'actividades_filtro.jsp')','','$response.encodeURL($url_prefix,'actividades.jsp')','ts=$ts&clean=true&filterdays='+document.getElementById('dayspainel').value);" >
							      #foreach($day in $days)
							         <option value="$count"> 
							         	$day
							         </option> 
							         #set($count=$count+1)
								  #end
							     </select>
						
								#if($hasFolder > 0)
									$folder_label
								    <select name="folderpainel" id="folderpainel" onchange="javascript:tabber(2, '$response.encodeURL($url_prefix,'actividades_filtro.jsp')','','$response.encodeURL($url_prefix,'actividades.jsp')','ts=$ts&clean=true&filterfolder='+document.getElementById('folderpainel').value);" >
								    <option value="0">$folder_default</option> 
								      #foreach($folder in $folders)
								         <option value="$folder.getFolderid()"> 
								         	$folder.getName()
								         </option> 
									  #end
								     </select>
								#end
								</p>
			- Acrescentar depois de <tr class="tab_header">
					            <td></td>
					            <td></td>											
	 		- Acrescentar depois de <tr class="tab_row_$class_type">
					             <td title='$act.get("task_annotation_color_title")'  style="background-color:$act.get("task_annotation_color_backgroundColor")" ></td>
					             <td>$act.get("task_annotation_icon")</td>
				             
				             
				             
V4.0.14 -> V4.0.15
 - É necessario actualizar o ficheiro iFlow_iFlowRemote.wsdl, na pasta "/iflow-home/repository_data/1/WSDL/iFlow_iFlowRemote.wsdl"
 - colocar no ficheiro main.vm: <script type="text/javascript" src="javascript/processAnnotations.js"></script
V4.0.13 -> V4.0.14
 - remover war V4.0.13 do tomcat (webapps e work)
 - instalar war v4.0.14 no tomcat
 - caso se pretenda que o a pesquisa possa se feita por todos os processos do user sem 
   limitar obrigatoriamente por fluxo colocar no iflow.properties: SEARCH_ALL_USER_PROCS_BY_DEFAULT=true
 - inserir no ficheiro iflow.properties as seguintes linhas e configurar de acordo com o pretendido:
	#Para expecificar o modo como os pedidos de delegações são comunicadas [notification,email,both,none]
	DELEGATION_NOTIFY_REQUEST_MODE=none
	#Para expecificar o modo como as confirmações de delegações são comunicadas [notification,email,both,none]
	DELEGATION_NOTIFY_ACCEPT_MODE=notification
	#Para expecificar o modo como as reatribuições de tarefas são comunicadas [notification,email,both,none]
	DELEGATION_NOTIFY_ASSIGN_MODE=notification
	#Para expecificar o modo como o apagar de delegações é comunicado [notification,email,both,none]
	DELEGATION_NOTIFY_DELETE_MODE=notification
 - Substituir na pasta repository_data\#orgid#\Messages os ficheiros web.properties, web_pt_PT.properties, 
   web_es_ES.properties e web_en_US.properties
 - Executar o script em .\db\upgrade_4.0.13_to_4.0.14.sql
 - � necess�rio inserir a chamada � applet e ao popup no .xsl para que de acordo com o parametro inserido no editor seja feita a respectiva chamada.
 					<xsl:if test="../file_sign_existing='true' and ../file_sign_method='false'">
	                  	<img class="toolTipImg" border="0" width="16" height="16" src="{$url_prefix}/images/sign.png" alt="Assinar" title="Assinar o documento original" >
							<xsl:attribute name="onclick">modifyFile('<xsl:value-of select="../variable" />','<xsl:value-of select="id"/>','<xsl:value-of select="../signatureType"/>','<xsl:value-of select="../encryptType"/>')</xsl:attribute>
	                  	</img>
					</xsl:if>
					<xsl:if test="../file_sign_existing='true' and ../file_sign_method='true'">
	                  	<img class="toolTipImg" border="0" width="16" height="16" src="{$url_prefix}/images/sign.png" alt="Assinar" title="Assinar documento" >
							<xsl:attribute name="onclick">
                                window.open('signpopup.jsp?oper=NaN&amp;docid=<xsl:value-of select="id"/>','Janela','toolbar=no,location=no,status=no,menubar=no,scrollbars=NO,resizable=NO,width=317,height=507'); return false;
							</xsl:attribute>
	                  	</img>
					</xsl:if>
					
                  	</td>
 
   
------------------------------------------------------------------------------
                           iFlow Upgrade How To                               
... -> ...
                           
------------------------------------------------------------------------------
                           iFlow Upgrade How To                               

V4.0.0 -> SQL SERVER, Compressão, Guardar apenas alterações
 - SQL SERVER: instalar o jar sqljdbc.jar (jdk 1.5) ou o jar sqljdbc4.jar (jdk 1.6) 
               executar o script sqlserver\init\create_datbase.sql
               criar o resouce e modificar a propriedade DB_POOL_NAME para o nom,e do novo resource
               modificar a propriedade DB_TYPE para SQLSERVER
 - MYSQL e ORACLE: executar o script upgrade_4.0.4_to_4.0.5.sql
 - Compactar process_history: colocar a propriedade COMPRESS_PROCESS_HISTORY=true
 - Guardar apenas alterações: colocar a propriedade ALLWAYS_SAVE_PROCESS_HISTORY=false
                           
------------------------------------------------------------------------------
                           iFlow Upgrade How To                               
V3.3.3-beta9 -> V4.0.0
 - remover war v3.3.3-beta9 do tomcat (webapps e work)
 - executar o script upgrade_3.3_to_4.0.0.sql
 - instalar war v4.0.0 no tomcat
                           
------------------------------------------------------------------------------

V3.3.3-beta8 -> V3.3.3-beta9
 - remover war v3.3.3-beta8 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17-beta7.jar para iFlowHome/repository_data/1/Classes
 - instalar war v3.3.3-beta9 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta7 -> V3.3.3-beta8
 - remover war v3.3.3-beta7 do tomcat (webapps e work)
 - instalar war v3.3.3-beta8 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta6 -> V3.3.3-beta7
 - remover war v3.3.3-beta6 do tomcat (webapps e work)
 - instalar war v3.3.3-beta7 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta5 -> V3.3.3-beta6
 - remover war v3.3.3-beta5 do tomcat (webapps e work)
 - instalar war v3.3.3-beta6 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta4 -> V3.3.3-beta5
 - remover war v3.3.3-beta4 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17-beta5.jar para iFlowHome/repository_data/1/Classes
 - copiar os ficheiros queries_MYSQL.properties e queries_ORACLE.properties para dbqueries
 - executar o script upgrade_3.3.3-beta3_3.3.3-beta5.sql
 - instalar war v3.3.3-beta5 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta3 -> V3.3.3-beta4
 - remover war v3.3.3-beta3 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17-beta4.jar para iFlowHome/repository_data/1/Classes
 - copiar libraries para repository:
     copiar repository_data/1/Libraries/*.xml para iFlowHome/repository_data/1/Libraries
     copiar repository_data/1_pt_PT/Libraries/*.xml para iFlowHome/repository_data/1_pt_PT/Libraries
 - instalar war v3.3.3-beta4 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta2 -> V3.3.3-beta3
 - remover war v3.3.3-beta2 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17-beta3.jar para iFlowHome/repository_data/1/Classes
 - Apenas MYSQL: executar o script upgrade_3.3.3-beta2_3.3.3-beta3.sql na base de dados
 - instalar war v3.3.3-beta3 no tomcat

------------------------------------------------------------------------------

V3.3.3-beta1 -> V3.3.3-beta2
 - remover war v3.3.3-beta1 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17-beta2.jar para iFlowHome/repository_data/1/Classes
 - executar o script upgrade_3.3.3-beta1_3.3.3-beta2.sql na base de dados
 - instalar war v3.3.3-beta2 no tomcat

------------------------------------------------------------------------------

V3.3.2-6 -> V3.3.3-beta1
 - remover war v3.3.2-3 do tomcat (webapps e work)
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.17.jar para iFlowHome/repository_data/1/Classes
 - copiar repository_data/1/Messages/web*.properties para iFlowHome/repository_data/1/Messages
 - copiar repository_data/1/Themes/proc_tasks.vm para iFlowHome/repository_data/1/Themes
 - copiar repository_data/1/Themes/proc_hist.vm para iFlowHome/repository_data/1/Themes
 - copiar repository_data/1/Libraries/*.xml para iFlowHome/repository_data/1/Libraries
 - copiar repository_data/1/Icons/* para iFlowHome/repository_data/1/Icons
 - copiar repository_data/1_pt_PT/Libraries/*.xml para iFlowHome/repository_data/1_pt_PT/Libraries
 - adicionar a propriedade HASHED_DOCUMENT_URL=false ao ficheiro iFlowHome/config/iflow.properties
 - adicionar a propriedade AUTO_ARCHIVE_CRON=0 0 3 * * ? ao ficheiro iFlowHome/config/iflow.properties
   (nota, executa diariamente as 03:00:00)
 - executar o script upgrade_3.3.2rc4_3.3.3rc1.sql na base de dados
 - instalar war v3.3.3-beta1 no tomcat

------------------------------------------------------------------------------

V3.3.2-5 -> V3.3.2-6
 - remover war v3.3.2-5 do tomcat (webapps e work)
 - instalar war v3.3.2-6 no tomcat

------------------------------------------------------------------------------

V3.3.2-4 -> V3.3.2-5
 - remover war v3.3.2-4 do tomcat (webapps e work)
 - instalar war v3.3.2-5 no tomcat

------------------------------------------------------------------------------

V3.3.2-3 -> V3.3.2-4
 - remover war v3.3.2-3 do tomcat (webapps e work)
 - remover o conteúdo do directório iFlowHome/repository_data/1/Classes
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.16-3.jar para iFlowHome/repository_data/1/Classes
 - instalar war v3.3.2-4 no tomcat

------------------------------------------------------------------------------

V3.3.2-2 -> V3.3.2-3
 - remover war v3.3.2-2 do tomcat (webapps e work)
 - remover o conteúdo do directório iFlowHome/repository_data/1/Classes
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.16-2.jar para iFlowHome/repository_data/1/Classes
 - Corrigir XSL existentes subtituindo _del_ por _rem_ (tipo 'file')
 - copiar flow-editor-1.3.2-1.one-jar.jar para iFlowHome/repository_data/1/WebFiles/floweditor.jar
 - copiar repository_data/1/StyleSheets/default.xsl para iFlowHome/repository_data/1/StyleSheets
 - copiar repository_data/1_pt_PT/StyleSheets/default.xsl para iFlowHome/repository_data/1_pt_PT/StyleSheets
 - copiar repository_data/1/Themes/proc_detail.vm para iFlowHome/repository_data/1/Themes
 - instalar war v3.3.2-3 no tomcat

------------------------------------------------------------------------------

V3.3.2-1 -> V3.3.2-2
 - remover war v3.3.2-1 do tomcat (webapps e work)
 - instalar war v3.3.2-2 no tomcat

------------------------------------------------------------------------------

V3.3.2 -> V3.3.2-1
 - remover war v3.3.2 do tomcat (webapps e work)
 - remover o conteúdo do directório iFlowHome/repository_data/1/Classes
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.16-1.jar para iFlowHome/repository_data/1/Classes
 - instalar war v3.3.2-1 no tomcat

------------------------------------------------------------------------------

V3.3.2rc4 -> V3.3.2
 - remover war v3.3.2rc4 do tomcat (webapps e work)
 - remover o conteúdo do directório iFlowHome/repository_data/1/Classes
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.16.jar para iFlowHome/repository_data/1/Classes
 - actualizar os ficheiros de mapeamentos LDAP/AD se necessário: 
   - ad_user_mappings.properties Acrescentar USERNAME=sAMAccountName
   - ldap_user_mappings.properties USERNAME=uid
 - actualizar os ficheiros de configuração AD e LDAP:
   - ad.properties acrescentar LIST_USERS=(&(objectCategory=person)(sAMAccountName=*)(!(userAccountControl:1.2.840.113556.1.4.803:=2)))
   - ldap.properties acrescentar LIST_USERS=(&(uid=*)(objectClass=sessionUser)(objectClass=posixAccount))
 - copiar os ficheiros queries_MYSQL.properties e queries_ORACLE.properties para iFlowHome/dbqueries
 - copiar o conteúdo do directório Icons para iFlowHome/repository_data/1/Icons
 - copiar o conteúdo dos directórios Library para iFlowHome/repository_data/1/Library e iFlowHome/repository_data/1_pt_PT/Library
 - copiar V3.3.2/flow-editor-1.3.2.one-jar.jar para iFlowHome/repository_data/1/WebFiles/floweditor.jar
 - instalar war v3.3.2 no tomcat

------------------------------------------------------------------------------

V3.3.2rc3 -> V3.3.2rc4
 - remover war v3.3.2rc3 do tomcat (webapps e work)
 - copiar o directório Messages para iFlowHome/repository_data/1
 - remover o conteúdo do directório iFlowHome/repository_data/1/Classes
 - copiar o conteúdo do ficheiro iflow-blocks-1.4.14.jar para iFlowHome/repository_data/1/Classes
 - copiar os ficheiros proc_detail.vm, top.vm e bottom.vm para iFlowHome/repository_data/1/Themes
 - copiar o conteúdo dos directórios Library para iFlowHome/repository_data/1/Library e iFlowHome/repository_data/1_pt_PT/Library
 - correr o script upgrade_3.3.2rc1_to_3.3.2_rc4.sql
 - caso os processos existentes utilizem ficheiros, fazer a migração, adaptando
   as queries comentadas em upgrade_3.3.2rc1_to_3.3.2_rc4.sql
 - actualizar os ficheiros de mapeamentos LDAP/AD se necessário
 - copiar os ficheiros queries_MYSQL.properties e queries_ORACLE.properties para iFlowHome/dbqueries
 - instalar war v3.3.2rc4 no tomcat

------------------------------------------------------------------------------

V3.3.2rc2 -> V3.3.2rc3
-remover war v3.3.2rc2 do tomcat (webapps e work)
-copiar o ficheiro top.vm pata iFlowHome/repository_data/1/Themes
-instalar war v3.3.2rc3 no tomcat

------------------------------------------------------------------------------

V3.3.2rc1 -> V3.3.2rc2
-remover war v3.3.2rc1 do tomcat (webapps e work)
-limpar directoria iFlowHome/repository_data/1/Classes e copiar conteúdo de iflow-blocks-1.4.13.jar para lá
-copiar Libraries, Themes, StyleSheets de V3.3.2rc2/repository_data/1 para iFlowHome/repository_data/1
-copiar Libraries, Themes, StyleSheets de V3.3.2rc2/repository_data/1_pt_PT para iFlowHome/repository_data/1_pt_PT
-acrescentar ao ficheiro ad.properties a propriedade STRIP_DOMAIN=false (ou true caso seja necessário retirar o dominio em logins AD)
-actualizar qualquer ficheiro costumizado (XSL, VM, etc) para codificar os URLs correctamente:
  - XSL: Qualquer public file e action da form (links??) - ver default.xsl
  - VM: Qualquer link, URI, request AJAX relacionado com o iFlow - ver top.vm
-instalar war v3.3.2rc2 no tomcat

------------------------------------------------------------------------------

V3.3.1rc5 -> V3.3.2rc1
-remover war v3.3.1rc5 do tomcat (webapps e work)
-limpar directoria iFlowHome/repository_data/1/Classes e copiar conteúdo de iflow-blocks-1.4.12.jar para lá
-copiar V3.3.2rc1/repository_data/1/Themes/task_list.vm para iFlowHome/repository_data/1/Themes/
-copiar V3.3.2rc1/flow-editor-1.3.2rc6.one-jar.jar para iFlowHome/repository_data/1/WebFiles/floweditor.jar
-correr script upgrade_3.3.1rc5_to_3.3.2rc1.sql na base de dados
-instalar war v3.3.2rc1 no tomcat

------------------------------------------------------------------------------

V3.3.1rc4 -> V3.3.1rc5

-remover war v3.3.1rc4 do tomcat (webapps e work)
-instalar war v3.3.1rc5 no tomcat
-limpar directoria iFlowHome/repository_data/1/Classes e copiar conteúdo de iflow-blocks.zip para lá
-copiar V3.3.1rc5/repository_data/1/Themes/task_list.vm e V3.3.1rc5/repository_data/1/Themes/top.vm V3.3.1rc5/repository_data/1/Themes/top.vm para iFlowHome/repository_data/1/Themes/
-copiar V3.3.1rc5/dbqueries/* para iFlowHome/dbqueries/*
-copiar V3.3.1rc5/repository_data/1/WebFiles/floweditormanual.pdf para iFlowHome/repository_data/1/WebFiles/ se não estiver
-copiar V3.3.1rc5/floweditor.jar para iFlowHome/repository_data/1/WebFiles
-acrescentar propriedade USE_EMAIL=true em iFlowHome/config/iflow.properties
