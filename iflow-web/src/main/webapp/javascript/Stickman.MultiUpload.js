/**
 * Multiple file upload element (Mootools 1.1 version)
 *  by Stickman
 *  http://the-stickman.com
 *  with thanks to:
 *   Luis Torrefranca -- http://www.law.pitt.edu
 *   and
 *   Shawn Parker & John Pennypacker -- http://www.fuzzycoconut.com
 *   ...for Safari fixes in the original version
 *
 * Licence:
 *  You may use this script as you wish without limitation, however I would 
 *  appreciate it if you left at least the credit and site link above in 
 *  place. I accept no liability for any problems or damage encountered 
 *  as a result of using this script. 
 *
 * Requires:
 *  Mootools 1.1 [ http://mootools.net ]
 *  ...with at least:
 *   Window.DomReady and its dependencies
 *   [ Download this release here: http://tinyurl.com/25ksor ]
 * Supports:
 *  All browsers supported by Mootools (see Mootools site for details)
 *
 * Usage:
 *  Include this file (or the packed version) and your mootools.js release in 
 *  your HTML file. To  convert a standard file input element into a multiple 
 *  file input element, add the following code to your HTML:
 * 
 *    window.addEvent('domready', function(){
 *      new MultiUpload( $( 'my_form' ).my_file_input_element );
 *    });
 *
 *  ...where 'my_form' is the ID of your form and 'my_file_input_element' is 
 *  the name of the file input element to be converted (or use whichever other
 *  method you prefer for finding the target file input element).
 *
 *  I've also included a simple CSS file (Stickman.MultiUpload.css) which
 *  you can include, although it's very basic (see 'Styling the element'),
 *  below.
 *
 * Optional parameters:
 *  There are four optional parameters (null = ignore this parameter):
 *
 *  - maximum number of files (default = 0)
 *    An integer to limit the number of files that can be uploaded using the 
 *    element. A value of zero means 'no limit'.
 *
 *  - File name suffix template (default '_{id}'
 *    By default, the script will take the name of the original file input 
 *    element and append an underscore followed by a number to it, eg. if the 
 *    input's name is 'file' then the elements will be numbered sequentially: 
 *    file_0, file_1, file_2... 
 *    You can change the format of the suffix by passing in a template. This 
 *    can be any string, but the sequence '{id}' will be replaced by the 
 *    sequential ID of the element. So if the element is called 'file' and you
 *    pass in the template '[{id}]' then the elements will be named file[0], 
 *    file[1], file[2]...
 *    To remove the suffix entirely, simply pass an empty string.
 *
 *  - Remove file path (default = false)
 *    By default, the entire path of the file is shown in the list of files. 
 *    If you would prefer to show only the file name, set this option to 
 *    'true'.
 *
 *  - Remove empty file input element (default = false)
 *    Because an extra (empty) element is created every time a file is 
 *    chosen, this means that there will always be one empty file input 
 *    element when the form is submitted. By default this is submitted with 
 *    the form (exactly as it would be with a 'normal' file input element, in 
 *    most browsers) but setting this option to 'true' will cause the element 
 *    to be disabled  (and therefore ignored) when the form is submitted.
 * 
 * Styling the element
 *  I didn't spend a lot of time making this look pretty. I've included an
 *  example CSS file (Stickman.MultiUpload.css) which is very basic but shows
 *  the parts that make up the element. These are:
 *   - div.multiupload
 *     When instaniated, the script places a container DIV around the file
 *     element, which also includes the files list
 *   - div.list
 *     Container DIV for the list of files
 *   - div.item
 *     Each item in the files list
 *   - div.item img
 *     The delete button image
 *  If changing the appearance of the element is not enough, you can alter the
 *  structure of the container and list elements in the initialize() method, 
 *  or the file list elements in the addRow() method.
 * 
 * Handling the uploaded files
 *  This is purely a client-side script -- I have not included any code for
 *  handling the uploaded files when they reach your server. This is because
 *  I don't know what platform you're using, or what you want to do with the
 *  files. When I posted the original version of this script, a lot of people 
 *  went on to submit support code for various platforms. So you might find
 *  what you need in the comments one of these pages:
 *   http://tinyurl.com/8yp53
 *   http://tinyurl.com/wrc8p
 *
 * Other notes
 *  Because it's not possible to  set the value of a file input element
 *  dynamically (for good security reasons), this script works by hiding the 
 *  file input element when a file is selected, then immediately replacing
 *  it with a new, empty one. This happens so quickly that it looks as if
 *  there's only ever one file input element. 
 *  Although ideally the extra elements would be hidden using the CSS setting
 *  'display:none', this causes Safari to ignore the element completely when
 *  the form is submitted. So instead, elements are moved to a position 
 *  off-screen.
 *  And no, it's not 'Ajax' -- it doesn't upload the files in the background
 *  or anything clever like that. Its sole purpose is cosmetic: to remove the 
 *  need for multiple file input elements in a form.
 */
var MultiUpload = new Class(
{
	/**
	 * Constructor
	 * @param		HTMLInputElement		input_element				The file input element
	 * @param		int						max							[Optional] Max number of elements (default = 0 = unlimited)
	 * @param		string					name_suffix_template		[Optional] Template for appending to file name. Use {id} to insert row number (default = '_{id}')
	 * @param		boolean					show_filename_only			[Optional] Whether to strip path info from file name when displaying in list (default = false)
	 * @param		boolean					remove_empty_element		[Optional] Whether or not to remove the (empty) 'extra' element when the form is submitted (default = true)
	 */
    initialize:function( input_element, max, name_suffix_template, show_filename_only, remove_empty_element, validExtensionStr, lang){
        if( $defined( validExtensionStr ) ){
            this.validExtensionStr = validExtensionStr;
        }
		// Sanity check -- make sure it's  file input element
		if( !( input_element.tagName == 'INPUT' && input_element.type == 'file' ) ){
			alert( this.getMessage('stickman_error', lang) );
			return;
		}

		// List of elements
		this.elements = [];
		// Lookup for row ID => array ID
		this.uid_lookup = {};
		// Current row ID
		this.uid = 0;

		// Maximum number of selected files (default = 0, ie. no limit)
		// This is optional
		if( $defined( max ) ){
			this.max = max;
		} else {
			this.max = 0;
		}

		// Template for adding id to file name
		// This is optional
		if( $defined( name_suffix_template ) ){
			this.name_suffix_template = name_suffix_template;
		} else {
			this.name_suffix_template= '_{id}';
		}

		// Show only filename (i.e. remove path)
		// This is optional
		if( $defined( show_filename_only ) ){
			this.show_filename_only = show_filename_only;
		} else {
			this.show_filename_only = false;
		}
		
		// Remove empty element before submitting form
		// This is optional
		if( $defined( remove_empty_element ) ){
			this.remove_empty_element = remove_empty_element;
		} else {
			this.remove_empty_element = false;
		}

		// Add element methods
		$( input_element );

		// Base name for input elements
		this.name = input_element.name;

        // Set up element for multi-upload functionality
        if ($defined(validExtensionStr)){
            this.initializeElement( input_element, lang, validExtensionStr);
        } else {
            this.initializeElement( input_element, lang );
        }

		// Files list
		var container = new Element(
			'div',
			{
				'class':'multiupload'
			}
		);
		this.list = new Element(
			'div',
			{
				'class':'list'
			}
		);
		container.injectAfter( input_element );
		container.adopt( input_element );
		container.adopt( this.list );
		
		// Causes the 'extra' (empty) element not to be submitted
		if( this.remove_empty_element){
			input_element.form.addEvent(
				'submit',function(){
					this.elements.getLast().element.disabled = true;
				}.bind( this )
			);
		}
	},

    checkIsValidExtension:function(extension, validExtensionsStr){
        var resultCode = 0;
        if ($defined( validExtensionsStr )){
            var splitChar = ';';
            var validExtensionsArray = validExtensionsStr.split(splitChar);

            for (i=0;i<validExtensionsArray.length;i++) {
                var indexOfDot = validExtensionsArray[i].indexOf(".");
                var verificationExtension = extension;
                if (indexOfDot != -1 && indexOfDot == 0){
                    verificationExtension = '.'+extension;
                }
                if (verificationExtension.toLowerCase() == validExtensionsArray[i].toLowerCase()){
                    resultCode = 1;
                }
            }
        } else {
            resultCode = 1;
        }
        return resultCode;
    },

	/**
	 * Called when a file is selected
	 */
    addRow:function(lang, validExtensions) {
		if( this.max == 0 || this.elements.length <= this.max ){
		
			current_element = this.elements.getLast();

			// Create new row in files list
			var name = current_element.element.value;
			// Extract file name?
			if( this.show_filename_only ){
				if( name.contains( '\\' ) ){
					name = name.substring( name.lastIndexOf( '\\' ) + 1 );
				}
				if( name.contains( '/' ) ){
					name = name.substring( name.lastIndexOf( '/' ) + 1 );
				}
			}

            if ($defined( validExtensions )){
                if (validExtensions != ''){
                    var extension = '';
                    if (name.contains('.')){
                        extension = name.substring( name.lastIndexOf( '.' ) + 1 );
                    }
                    var isValidExtension = this.checkIsValidExtension(extension, validExtensions);
                } else {
                    var isValidExtension = 1;
                }
            } else {
                isValidExtension = 1; // é true;
            }

            if (isValidExtension){
    			var item = new Element(
    				'span'
    			).setText( name );
    			var delete_button = new Element(
    				'img',
    				{
    					'src':'../images/cross_small.gif',
    					'alt':this.getMessage('stickman_delete', lang),
    					'title':this.getMessage('stickman_delete', lang),
    					'events':{
    						'click':function( uid ){
    							this.deleteRow( uid, lang );
    						}.pass( current_element.uid, this )
    					}
    				}
    			);
    			var row_element = new Element(
    				'div',
    				{
    					'class':'item'
    				}
    			).adopt( delete_button ).adopt( item );
    			this.list.adopt( row_element );
    			current_element.row = row_element;
    			
    			// Create new file input element
    			var new_input = new Element
    			(
    				'input',
    				{
    					'type':'file',
    					'disabled':( this.elements.length == this.max )?true:false
    				}
    			);
    			// Apply multi-upload functionality to new element
    			this.initializeElement(new_input, lang);

    			// Add new element to page
    			current_element.element.style.position = 'absolute';
    			current_element.element.style.left = '-1000px';
    			new_input.injectAfter( current_element.element );
            } else {
                current_element.element.value = "";
                alert(this.getMessage('stickman_invalidEx', lang));
            }
		} else {
		    element.name="";
			alert( this.getMessage('stickman_upload_max', lang).replace("{0}", "" + this.max));
		}
		
	},

	/**
	 * Called when the delete button of a row is clicked
	 */
	deleteRow:function( uid, lang ){
		// Confirm before delete
		deleted_row = this.elements[ this.uid_lookup[ uid ] ];
		if( confirm( this.getMessage('stickman_delete_confirm', lang).replace("{0}", "" + deleted_row.element.value) ) ){
			this.elements.getLast().element.disabled = false;
			deleted_row.element.remove();
			deleted_row.row.remove();
			// Get rid of this row in the elements list
			delete(this.elements[  this.uid_lookup[ uid ] ]);

			// Rewrite IDs
			var new_elements=[];
			this.uid_lookup = {};
			for( var i = 0; i < this.elements.length; i++ ){
				if( $defined( this.elements[ i ] ) ){
					this.elements[ i ].element.name = this.name + this.name_suffix_template.replace( /\{id\}/, new_elements.length );
					this.uid_lookup[ this.elements[ i ].uid ] = new_elements.length;
					new_elements.push( this.elements[ i ] );
				}
			}
			this.elements = new_elements;
		}
	},

	/**
	 * Apply multi-upload functionality to an element
	 *
	 * @param		HTTPFileInputElement		element		The element
	 */
    initializeElement:function( element, lang, validExtensions ){
		// What happens when a file is selected
		element.addEvent(
			'change',
			function(){
                this.addRow(lang, validExtensions);
			}.bind( this )
		);
		// Set the name
		element.name = this.name + this.name_suffix_template.replace( /\{id\}/, this.elements.length );

		// Store it for later
		this.uid_lookup[ this.uid ] = this.elements.length;
		this.elements.push( { 'uid':this.uid, 'element':element } );
		this.uid++;
	
	},
	
	getMessage:function(message, lang) {
		var enUS = 'en_US';
		var ptBR = 'pt_BR';
		var defeito = ptBR;
		if (lang) {
			if(lang.indexOf('en') >= 0 || lang.indexOf('US') >= 0) {
				lang = enUS;
			} else if(lang.indexOf('pt') >= 0 || lang.indexOf('BR') >= 0) {
				lang = ptBR;
			} else {
				lang = defeito;
			}
		} else {
			lang = defeito;
		}
		var messages = {
				stickman_error:{
					pt_BR: 'Erro: não é um elemento de entrada de ficheiro',
			    	en_US: 'Error: not a file input element'
			    },
				stickman_upload_max:{
			    	pt_BR: 'Não pode carregar mais do que {0} ficheiros',
			    	en_US: 'You may not upload more than {0} files'
			    },
				stickman_delete_confirm:{
			    	pt_BR: 'Tem a certeza que quer remover o item\r\n{0}\r\nda lista de carregamento?',
			    	en_US: 'Are you sure you want to remove the item\r\n{0}\r\nfrom the upload queue?'
			    },
				stickman_delete:{
			    	pt_BR: 'Apagar',
			    	en_US: 'Delete'
			    },
                stickman_invalidEx:{
                    pt_BR: 'Esse tipo de ficheiro, não é permitido.',
                    en_US: 'File type, not allowed'
                },
			    'undefined': {
			    	pt_BR: 'indefinido',
			    	en_US: 'undefined'
			    }
			};
		if(messages[message][lang]) {
			return (messages[message][lang]);
		} else {
			return (messages['undefined'][lang]);
		}
	}
}
);