		var userId;
        // Désactivation du clic droit
        document.addEventListener('contextmenu', event => event.preventDefault());
       
        var launchTerm;
        var currentTerminal;
        var lastKey;
        var lastData="";
        var term;
        var connection;
		var termZIndex=200;
		
		
		reconnect=true;
        
        $(document).ready(function () {
        	

		

        	// Application du theme sauvegardé
			//var currentTheme=getValueFromAction("AdminAction?function=getTheme",false);
			var currentTheme="Amiga";
			applyTheme(currentTheme);
			
            //get instance id list from selected terminals
            function getActiveTermsInstanceIds() {
            	if ( typeof currentTerminal != "undefined" && currentTerminal != "none")
            		{
            		//log("Current Terminal : " + currentTerminal);
            		return currentTerminal.replace("shellbox_","");
            		}
            	return "none";
            }

			var progressShow=0;
			var selectionText="";

		
			
			
			
			//$("#workbench1").css("width",($(window).width()-13) + "px");$("#w1").css("width",($(window).width()-13) + "px");
			//$("#workbench2").css("width",($(window).width()+10) + "px").css("left","10px");$("#w2").css("width",($(window).width()+10) + "px").css("left","10px");
			//$("#workbench3").css("width",($(window).width()+10) + "px");$("#w3").css("width",($(window).width()+10) + "px");
					
		
			// Connection a la socket
			
			
			connectWeb();
			
        	
            var termMap = new Map();
         
            
          	var intervalSocket = setInterval(function() {
				connectWeb()
					}, 10000);
        	
        	





			 
          	var intervalSaveWorkbench = setInterval(function() {
				saveWorkBench()
					}, 10000);


			


    		// Fonction permettant d'ouvrir la websocket
            function connectWeb()
            {
            var loc = window.location, ws_uri;
            
          
			
			if ( reconnect )
			{ 
				var port=4433;
				getValueFromUrl("admin?action=getWebSocketPort",true,function(aJson){
						port=aJson;
						ws_uri = "wss://" + loc.host + ':' + port +'/adminwebsocket';	
							
						$("#guruFail").show();
						log("Connection " + ws_uri + " socket:" + connection);
			            connection = new WebSocket(ws_uri);
						$("#guruFail").hide();
						reconnect=false;
			    		// Log errors
			            connection.onerror = function (error) {
			                log('WebSocket Error ' + error);
							reconnect=true;
							$("#guruFail").show();
			            };
						
			            // Log messages from the server
			            connection.onmessage = function (e) {
			              	var json = jQuery.parseJSON(e.data);
			              	$.each(json, function (key, val) {
			            	 	if (val.output != '') 	{
										                 	log("Fill #terminal_" + val.instanceId + " with " + val.output);
								                 	 		term=termMap.get("shellbox_" + val.instanceId);
													 		log(term);
								 							if ( typeof term == "undefined")
															{
																log("#terminal_" + val.instanceId + " doesn't exist anymore");
															}
			                 	 							term.write(val.output);
			                     							$("#terminal_" + val.instanceId).append();
															scroll=$("#terminal_" + val.instanceId)[0].scrollHeight;
															$("#terminal_" + val.instanceId).scrollTop(scroll);
			             								}
			              	});
					    };
					    
					    connection.onclose== function (e) {
			            									log("On close " + e);
			           	 };
						});
			            	}else
			            	{
							$("#guruFail").hide();
			            	log("Socket:" + connection + " Still alive");
			            };
				
            } 
            
           

            
            var keys = {};

            
          
 			function saveWorkBench()
			{
				workbenchs=new Map();
				$('body').find(".workbench").each(function() {
					//log("Saving " + $( this ).attr('id'));
  					workbenchs[$(this).attr('id')]=$(this).html();
				});
				
				getValueFromActionPost("admin/action=saveWorkbenchs",true,JSON.stringify(workbenchs),"saveWorkbenchs");
			} 
           
            
            // Désactivation touche back (delete), F1 , F2 et F
            $(document).keydown(function(e) {
	            	
	                if (e.keyCode == 8 || e.keyCode == 112 || e.keyCode == 113 || e.keyCode == 114) {
						functionKey(e);
	                    return false;
	                };
	            });
          
            
			

           	
			// Inhibition du bouton du milieu et droit
			$("body").delegate(".terminalPimp", 'mousedown', function(e){
				if(e.button==1 || e.button==2 )
					{
					log("Copier / coller de la selection [" +  selectionText + "] ");
					try{
					if ( getActiveTermsInstanceIds() != "none")
						{
						connection.send(JSON.stringify({id: getActiveTermsInstanceIds(), command: selectionText, user: userId}));
						}
					}catch (e)
					{
						
					}
					return false;
					}
			});
			
			function manageCtrl(keyCode)
			{
				feedback=true;
				if ( keyCode == "Control" )
					{
						//log("It's a control hit");
					 	feedback=false;
						switch (lastKey) {
									case 'c':
											//log("send a c 67 code");
											sendMessage(67,"none");
											break;
									case 'z':
											//log("send a c 90 code");
											sendMessage(90,"none");
											break;
										}
					}
				
				return feedback;
			}
			
//			$("body").delegate(".output", 'keypress', function(e){
				$(document).keypress(function (e) {
            	//alert("keypress" + e.charCode + " termFocus " + termFocus + " loginBoxFocus " + loginBoxFocus);
					
                	
                    var keyCode = (e.keyCode) ? e.keyCode : e.charCode;
					
                    //log("keypress : " + e.charCode + " " + keyCode);
					lastKey=e.key;
					//log("lastKey " + lastKey);
                    
                    
	                    if (String.fromCharCode(keyCode) && String.fromCharCode(keyCode) != ''
	                            && !keys[91] && !keys[93] && !keys[224] && !keys[27]
	                            && !keys[37] && !keys[38] && !keys[39] && !keys[40]
	                            //&& !keys[13] && !keys[8] && !keys[9] && !keys[17] && !keys[46]) {
	                    	 // Suppression inhibe du pipe keys[17]
	                         && !keys[13] && !keys[8] && !keys[9] && !keys[46]) {
	                    	
	                        var cmdStr = String.fromCharCode(keyCode);
	                        sendMessage("none",cmdStr);
	                    }
                    
                
            });
            //function for command keys (ie ESC, CTRL, etc..)
				$("body").keydown(function (e) {
					//log("keydown " + e.key);
					
					if ( manageCtrl(e.key))
                    {
						lastKey=e.key;
						//log("lastKey " + lastKey);
						if (e.key == "Tab" || e.key == "Backspace" || e.key == "ArrowUp" || e.key == "ArrowDown" ) {
							event.preventDefault();
							var keyCode = (e.keyCode) ? e.keyCode : e.charCode;
								sendMessage(keyCode,"none");
								return;
						}
                    }
				});

//            $(body).keydown(function (e) {
            	 $("body").delegate(".terminalPimp .amigaBoxHeader", 'keydown', function(e){ 
					  
            		 //log("keydown " + e.charCode + " " + String.fromCharCode(e.keyCode));
              
            		
            		
                    var keyCode = (e.keyCode) ? e.keyCode : e.charCode;
                    keys[keyCode] = true;
                    //prevent default for unix ctrl commands
                    if (keys[17] && (keys[83] || keys[81] || keys[67] || keys[220] || keys[90] || keys[72] || keys[87] || keys[85] || keys[82] || keys[68])) {
                        e.preventDefault();
                    }
                    
//                    if (keys[13] )
//                    	{
//                    $('#session_size_'+getActiveTermsInstanceIds()).html(getValueFromAction("admin?action=getSessionSize&instanceId=" + getActiveTermsInstanceIds(),false) + " ko");
//                    	}

                    //27 - ESC
                    //37 - LEFT
                    //38 - UP
                    //39 - RIGHT
                    //40 - DOWN
                    //13 - ENTER
                    //8 - BS
                    //9 - TAB
                    //17 - CTRL
                    //46 - DEL
                    if (keys[27] || keys[37] || keys[38] || keys[39] || keys[40] || keys[13] || keys[8] || keys[9] || keys[17] || keys[46]) {
                    	sendMessage(keyCode,"none");
                    }
              
            });
            
            function sendMessage(keyCode,command)
            {
            	if ( getActiveTermsInstanceIds() != "none")
				{
					//log("Trying sending...");
            		if ( keyCode != "none" )
            			{
            			//log("connection.send.keycode " + JSON.stringify({id: getActiveTermsInstanceIds(), keyCode: keyCode}));
                        connection.send(JSON.stringify({id: getActiveTermsInstanceIds(), keyCode: keyCode, user: userId}));
            			}
            		if ( command != "none" )
        			{
            		  //log("connection.send " + JSON.stringify({id: getActiveTermsInstanceIds(), command: command}));
                      connection.send(JSON.stringify({id: getActiveTermsInstanceIds(), command: command, user: userId}));
        			}            				
				}else
				{
					//log("Not sending because getActiveTermsInstanceIds() returns none");
				}
            	
            }
           
            $("body").delegate(".terminal", 'keyup', function(e){       
//            $(document).keyup(function (e) {
				//termMap.get(currentTerminal).focus();
            	//log("Document.keyup");
			
                var keyCode = (e.keyCode) ? e.keyCode : e.charCode;
               
                delete keys[keyCode];
               
            });

            $("body").delegate(".aShellBox", 'click', function(e){  
				      
            		var id=$(this).attr('id')
            		currentTerminal=id;
					termMap.get(id).focus();
					//log("Current terminal " + id + " from aShellBox ");
					termZIndex++;
					$(this).css("z-index",termZIndex);
					
					 
            });

			   $("body").delegate(".terminal", 'click', function(e){
				
					var id=$(this).parent().attr('id')
					currentTerminal=id.replace("terminal","shellbox");            
					$(this).focus();
            		//log("Current terminal " + currentTerminal + " from terminal");
					termZIndex++;
					$(this).css("z-index",termZIndex);
            });



       
        	
        	$("body").delegate(".terminal", 'mouseup',function(){
				//log("mouseup on terminal");
			
        		getSelectionText();
     		});
        

			$("body").delegate(".workbench", 'click',function(){
				//log("click on workbench");
        	
     		});

        	
        	// Permet de flusher un historique (allÃ¨ge les sessions)         
        	$("body").delegate("div.cleanable", 'click', function(){
    			var id=$(this).attr('id').replace("session_clean_", "");
    			getValueFromAction("AdminAction?function=flushSessionHistory?instanceId=" + id,true);
    			$('#session_size_'+id).html(getValueFromAction("admin?action=getSessionSize&instanceId=" + id,false) + " ko");
     		});
        	
        	// Permet de fermer une session         
        	$("body").delegate(".closeable", 'click', function(e){
					
					var name=$(this).attr('name')
					//log("Close " + name + " " + name.indexOf("shellbox_") );
					// Only for session case
					if ( name.indexOf("shellbox_") >= 0)
					{
						$("#" + name).remove();
						sessionId=name.replace("shellbox_","");
						//log("Closing session " + sessionId);
						getValueFromAction("admin?action=closeSession&sessionId=" + sessionId,true);
					}else
					{
						$(this).parent().hide();
					}
			});

            //resize element
            function resize(element) {
				//log("resize " + element.attr("id") + " and " + element.parent().attr("id"));
				resize_height=element.css("height").replace("px","") - 50;
				//log("Height: " + resize_height);
				$("#" + element.attr("id") + "BoxContainer").css("height",resize_height + "px");
				$("#" + element.attr("id").replace("shellbox","terminal")).css("height",resize_height + "px");
				
				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".terminal").first().css("height",resize_height + "px");
				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-viewport").first().css("height",resize_height + "px");
				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-scroll-area").first().css("height",resize_height + "px");
				
				//log($("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-scroll-area").first());
				
							
				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-screen").first().css("height",resize_height + "px");
				
				sessionId=element.attr("id").replace("shellbox_","");
				
				cols=200;
				rows=24;
				wp=800;
				hp=600;
				getValueFromAction("admin?action=changeSizeSession&sessionId=" + sessionId + "&cols=" +cols+"&rows=" + rows + "&wp=" + wp + "&hp=" +hp,true);
				
				if ( currentTerminal != "none")
				{
					termMap.get(currentTerminal).fit();
				}
				
//				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-helpers").first().css("height",resize_height + "px");
//				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-text-layer").first().css("height",resize_height + "px");
//				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-selection-layer").first().css("height",resize_height + "px");
//				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-link-layer").first().css("height",resize_height + "px");
//				$("#"  + element.attr("id").replace("shellbox","terminal")).find(".xterm-cursor-layer").first().css("height",resize_height + "px");
				
				
				
//            	id=element.attr("id").replace("shellbox_","");
//            		height=element.height() - 50;
//                	log("resize " + id + " to " + height);     
//                	$("#terminal_" + id).css("height",height +"px");
					
					//
                    pushSizeAndPosition(element);
                 }

  
            
            
            function restoreSizeAndPosition(element)
            {
//            	 var retour=getValueFromAction("AdminAction?function=getTerminalInfo?instanceId=" + instanceId,false);
//            	 var json = jQuery.parseJSON(retour);
//            	 $("#_" + instanceId).width(json.termWidth);
//            	 $("#_" + instanceId).height(json.termHeight);
//            	 $("#_" + instanceId).offset({ top: json.termYPosition, left: json.termXPosition});
//            	 $("#_" + instanceId).zIndex(0);
            	 
            	
            }
          
            
            function getSelectionText() {
				
                var text = "";
                if (window.getSelection) {
                    text = window.getSelection().toString();
                } else if (document.selection && document.selection.type != "Control") {
                    text = document.selection.createRange().text;
                }
                if ( text.trim() != "")
                	{
                	selectionText=text.trim();
                	}
				//log("selection text " + text );
                return text;
            }

         
            //function to set all AmigaBox event bindings
            function setTerminalEvents(element)
            {
             
            }
            
            //function to set all  event bindings
            function setBoxEvents(id)
            {
            	//log("setAmigaBoxEvents on " + id);
                // set draggable
                $(id).draggable(
                		{
                			 handle: id + "BoxTitle", 
                			 opacity: 0.7, helper: "original",
						      start: function(e) {
							      
                		    	  //log("Stop Drag " + $(e.target).attr('id') );
                		    	  pushSizeAndPosition($(e.target).attr('id'));
               		      },
                		      stop: function(e) {
                		    	  //log("Stop Drag " + $(e.target).attr('id') );
                		    	  pushSizeAndPosition($(e.target).attr('id'));
									
                		      }
                		}
                		
                );
              
                
                $(id).resizable({
                	helper: "ui-resizable-helper",
                    ghost: true,
					start: function (event,ui) {
			      
					},
                    stop: function (event, ui) {
                        resize($(this));
                    }	
                });
            }
 
       
           
       
            
           
            
            
            function pushSizeAndPosition(name)
            {
            	
//            	var instanceId= $('#' + name).attr("id").replace("_", "");
//            	var width=$('#' + name).width();
//            	var height=$('#' + name).height();
//            	var position = $('#' + name).offset();
//            	var x_position=position.left;
//            	var y_position=position.top;
//            	var z_index=$('#' + name).css("z-index");
//            	  
//                getValueFromAction("AdminAction?function=setPositionAndSize?instanceId=" + instanceId + "&termWidth=" + width + "&termHeight=" + height + "&termXPosition=" +x_position+ "&termYPosition=" +y_position+"&zindex=" + z_index,true);
//            	
            }
            
			// Case of reloading Term from the deads
            rebindTerm=function rebindTerm(workbenchId)
			{
					log("Repawning from " + workbenchId);
					data=$("#" + workbenchId).html();
					$("#" + workbenchId).children().find(".aShellBox").each(function() {
        				id=$(this).attr("id");
						log("Respawn " + id);
						$(this).css("display","block");
						term=bindTerm(id.replace("shellbox_",""),"");
						
						log("Fill term with past-data " + currentUser.sessionsData[id.replace("shellbox_","")][0]);
//						for (i=0;currentUser.sessionsData[id.replace("shellbox_","")].length;i++)
//						{
//							//log(currentUser.sessionsData[id.replace("shellbox_","")][i]);
//							term.write(currentUser.sessionsData[id.replace("shellbox_","")][i]);
//						}
						term.write(currentUser.sessionsData[id.replace("shellbox_","")][0]);
						
						
    				});
					
					//$("#" + workbenchId).children().find(".aShellBox").css("display","block");
			}
			
			function bindTerm(instanceId,connectMessage)
			{
				
				Terminal.applyAddon(fit);
                 term = new Terminal({
     				cols: 80,
     				rows: 24,
					allowTransparency: true,
					bellStyle:"none",
					cursorBlink: true,
					fontSize: "12",
					rightClickSelectsWord: false,
					
     			 });
				 $("#terminal_" + instanceId).html("");
                 term.open($("#terminal_" + instanceId)[0]);
				 term.on('selectionChange', function(key, e) {
					  alert(key,e);
				 });
				 term.write(connectMessage);
			     term.fit();
                 term.setOption('theme', { background: '#bbbbbb', foreground: '#000000' });
				
				 //term.setOption('fontSize', 12)
    			 //term.setOption('scrollback', 1000000)
    			 //term.setOption('disableStdin', true)
    			 //term.setOption('convertEol', true)


                 termMap.set("shellbox_" + instanceId,term);
                 currentTerminal="shellbox_" + instanceId;
                 setBoxEvents("#shellbox_"+instanceId); 
                 setTerminalEvents($("#terminal_" + instanceId));
				 return term;
			}
			
			            
            // Création d'un terminal de toute pièce
            launchTerm=function launchTerm(session,workbenchId)
            {
            	
            	
            	 // Récupération d'une nouvelle instance Id
                 var instanceId=getValueFromAction("admin?action=getNextSessionId",false);
                 
                 var displayLabel="AmigaShell " + session.sessionId + " idSession=" + instanceId;
                 connectMessage=connectMessageDecoration(session.sessionId);
                 instance=terminalDecoration("shellbox_" + instanceId, displayLabel, 600, 400, "position:absolute;top:200px;left:100px","aShellBox",
                         "<pre style=position:relative;z-index:200;height:350px;top:-12px id=terminal_" + instanceId + " class=\"terminalPimp selectable\"></pre>");

				 log("Add terminal in #termContainer" + workbenchId);	
			
                 $("#termContainer" + workbenchId).append(instance);
				
				 if (session.transparent)
					{
						$("#shellbox_" + instanceId).css('opacity',0.7);
					}
				 
                 
				 bindTerm(instanceId,connectMessage);
				
			
//                 $("#terminal_" + instanceId).removeClass("noselect");
                 getValueFromAction("admin?action=createSession&sessionId=" + session.sessionId + "&instanceId=" +instanceId,true);

            }
 
            String.prototype.replaceAll = function(search, replacement) {
                var target = this;
                return target.replace(new RegExp(search, 'g'), replacement);
            };
            
            String.prototype.removeAll = function(remove) {
                var sentence = this;
                while (true) {
        		    index = sentence.indexOf(remove);
        		    if (index == -1) {
        		       break;
        		    }else {
        		       sentence = sentence.replace(remove,"");
        		    }
        		}
        		return sentence;
            };
            
            String.prototype.subAll = function(remove,newstring) {
                var sentence = this;
                while (true) {
        		    index = sentence.indexOf(remove);
        		    if (index == -1) {
        		       break;
        		    }else {
        		       sentence = sentence.replace(remove,newstring);
        		    }
        		}
        		return sentence;
            };
            
            function antiJam(data)
            {
            	return data;                     
            }
            
            
            
           
            function convert(data,instanceId)
            {
            	
            	
            }
            
            
           
        	function countOcc(sentence,pattern)
        	{
        		count = 0; 
        		while (true) {
        		    index = sentence.indexOf(pattern);
        		    if (index == -1) {
        		       break;
        		    }else {
        		       ++count;
        		       sentence = sentence.substring(index + pattern.length);
        		    }
        		}
        		return count;
        	}

        });