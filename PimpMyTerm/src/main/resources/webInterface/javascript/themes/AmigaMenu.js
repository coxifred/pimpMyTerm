/**
 * AmigaMenu.js
 */
var ztop = 1000;
var usedHeap = "0,000,000";
var maxSizeHeap = "0,000,000";
var currentUser;
var sessions = new Map();
var terminalDecoration;
var functionKey;
var currentWorkbench="workbench1";


var currentSelectedObject;

$(document).ready(function () {

function numberWithCommas(value) {
	return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

var intervalMemory = setInterval(function() {
	displayMemory()
}, 10000);


function addContainer(workbench)
{
	workbenchId=workbench.replace("workbench","");
	if ( $("#" + workbench).html().indexOf("termContainer") < 0 )
	{
	$("#" + workbench)
			.append("<div id=\"termContainer" + workbenchId + "\" class=\"term-container\"></div>");
	}
}

function addMenu(workbench) {
	$("#" + workbench)
			.append(
					"<div class=\"menuAmiga noselect\"><img class=ballAmigaMenu src=/images/themes/Amiga/amiga_ball.png><div id=textStateAmiga class=textStateAmiga>Amiga Workbench</div><div id=memory class=memory></div><i  name=1 class=\"tv icon layer1 layer\"></i><i  name=2 class=\"tv icon layer2 layer\"></i><i  name=3 class=\"tv icon layer3 layer\"></i></div>");
	displayMemory();
}



function addContextMenu(workbench) {
	// Populate general menu
	$("#" + workbench)
			.append(
					"<div class=\"menuContextAmiga noselect\"><div id=workBenchMenu class=\"workBenchMenu  surlignable\">Workbench</div><div id=windowMenu class=\"windowMenu surlignable\">Window</div><div id=toolMenu class=\"toolMenu surlignable\">Tools</div></div>");
	// Populate sub menu for Workbench
	$("#" + workbench)
			.append(
					"<div  class=\"workBenchMenuContext noselect\"><div id=sessionsMenu class=\"surlignable sessionsMenu\" >Sessions<img src=/images/themes/Amiga/subMenu.png id=subSessionIcon class=subSessionIcon></div><div class=\"surlignable optionsMenu\"'>Options<img src=/images/themes/Amiga/subMenu.png id=subOptionsIcon class=subOptionsIcon></div><hr><div class=\"surlignable aboutMenu\">About</div><hr><div  class=\"quit surlignable\">Quit</div></div>");
	// Display subsession menu
	$("#" + workbench)
			.append(
					"<div  class=\"sessionsMenuContext noselect\"><div id=sessionsManager class=\"surlignable sessionsManager\" >Sessions Manager</div><hr><div id=sessionList class=sessionList></div></div>");
	// Display suboptions menu
	$("#" + workbench)
			.append(
					"<div  class=\"optionsMenuContext noselect\"><hr><div name=workbench1 class=\"surlignable resetWorkbench\" >Reset factory left workbench</div><div name=workbench2 class=\"surlignable resetWorkbench\" >Reset factory center workbench</div><div name=workbench3 class=\"surlignable resetWorkbench\" >Reset factory right workbench</div><div name=all class=\"surlignable resetWorkbench\" >Reset factory all workbenchs</div><hr></div>");				
	// Populate window Menu
	$("#" + workbench)
			.append(
					"<div class=\"windowMenuContext noselect\"><div id=reorganize class=surlignable >Reorganize</div><div id=minimize class=surlignable >Minimize</div><div id=closeAll class=surlignable >Close all</div></div>");
	// Populate tools menu
	$("#" + workbench)
			.append(
					"<div class=\"toolMenuContext noselect\"><div id=timberwolf class=surlignable >Timberwolf</div><hr><div id=musicMenu class=surlignable >Sounds<img src=/images/themes/Amiga/subMenu.png id=subSoundIcon class=subSoundIcon></div><div id=themeMenu class=surlignable >Themes<img src=/images/themes/Amiga/subMenu.png id=subThemeIcon class=subThemeIcon></div></div>");
	// Display subsound menu
	$("#" + workbench)
			.append(
					"<div class=\"soundMenuContext noselect\"><div id=stopSound class=\"surlignable\">Stop</div><hr></div>");
	// Display subtheme menu
	$("#" + workbench)
			.append(
					"<div class=\"themeMenuContext noselect\"><div id=amigaTheme class=\"surlignable\">Amiga</div><hr></div>");
}

				function callbackOut() {
      										setTimeout(function() {
        										$( "#effect:visible" ).removeAttr( "style" ).fadeOut();
      												}, 1000 );
   										 };
				function callbackIn() {
      										setTimeout(function() {
        										$( "#effect:visible" ).removeAttr( "style" ).fadeIn();
      												}, 1000 );
   										 };

				function displayWorkbench(id)
										{
											direction="right";
											directionOld="left";
											
											if ( currentWorkbench.replace("workbench","") > id)
											{
												direction = "left";
											
											} else if (  currentWorkbench.replace("workbench","")  == id)
											{
												direction= "up";
											
											} else
											{
												direction = "right";
											
											}
											currentWorkbench="workbench" + (id);
  											log('slide changed to ' + currentWorkbench + " id=" + id);
									
											$('body').find(".layer").addClass("disabled");
											var options = {direction: direction};
											$('body').find(".workbench").hide();
											$( "#workbench" + id ).show( "slide", options, 600, callbackOut );
											
											$('body').find(".layer" + id).removeClass("disabled");
										}

functionKey=function(key)
{
	log("AmigaTheme key pressed " + key.keyCode);
	if ( key.keyCode == "112" )
	{
		displayWorkbench("1");
	} else if ( key.keyCode == "113" )
	{
		displayWorkbench("2");
	} else if ( key.keyCode == "114" )
	{
		displayWorkbench("3");
	} 
}


terminalDecoration=function (id,title,width,height,style,classes,content)
{
	log("terminalDecoration " + id + title + width + height + style + classes + content);
	return amigaBox(id,title,width,height,style,classes,content);
}

connectMessageDecoration=function (id)
{
	return "New Shell process " + id + "\n\r" + id + ".System:> version SYS:Kickstart/console.device.kmode full\n\rconsole.device 53.83 (23/07/1985)\n\r"
}

amigaBox=function amigaBox(id,title,width,height,style,classes,content,styleContainer)
{
	return "<div style=\"height:" + height+ ";width:" + width +";" + style + "\" id=\"" + id + "\" class=\"amigaBox noselect " + classes + "\">" + amigaBoxHeader(id,title) + amigaBoxContainer(id,content,width,height,styleContainer) + "</div>"
}

amigaBoxContainer=function amigaBoxContainer(id,content,width,height,styleContainer)
{
	return "<div id=\"" + id + "BoxContainer\" style=\"" + styleContainer + "\" class=\"amigaBoxContainer noselect\">" +  content + "</div>"
}

header=function header(id,title,classes)
{
 return "<div id=" + id + "BoxTopLeftCorner class=\"amigaBoxTopLeftCorner noselect " + classes + "\"/><div id=" + id + "BoxHeader class=\"amigaBoxHeader noselect\"/><div id=" + id + "BoxTitle class=\"amigaBoxTitle\">" + title + "</div><div id=" + id + "BoxTopRight2Corner class=\"amigaBoxTopRight1Corner\"/><div id=" + id + "BoxTopRight2Corner class=\"amigaBoxTopRight2Corner\"/>";
}

amigaBoxHeader=function amigaBoxHeader(id,title,classes)
{
 return "<div name=" + id + " id=" + id + "_BoxClose class=\"amigaBoxTopLeftCorner noselect closeable " + classes + "\"/><div id=" + id + "BoxHeader class=\"amigaBoxHeader noselect\"/><div id=" + id + "BoxTitle class=\"amigaBoxTitle\">" + title + "</div><div id=" + id + "BoxTopRight2Corner class=\"amigaBoxTopRight1Corner\"/><div id=" + id + "BoxTopRight2Corner class=\"amigaBoxTopRight2Corner\"/>";
}

amigaList=function amigaList(id,width,height,style,classes)
{
 return "<div style=\"width:" + (width) + "px;height:" + (height) + "px;" + style + "\" class=\"amigaListPerimeter noselect " + classes + "\" ><div id=" + id + " style=\"position:relative;top:0px;left:0px;width:" + (width -2 ) + "px;height:" + (height - 2) + "px\" class=\"amigaListBack\" ></div></div>";
}

amigaButton=function amigaButton(id,title,style,classes)
{
 return "<div style=\"" + style + "\" class=\"amigaButtonPerimeter\" ><div class=\"amigaButtonBack noselect\" ><div id=" + id + "Button class=\"amigaButton " + classes + "\">" + title + "</div></div></div>";
}

amigaText=function amigaText(text,style,classes)
{
	return "<div style=\"" + style + "\" class=\"amigaText " + classes + "\" >" + text + "</div>";
}

amigaTextField=function amigaTextField(id,text,style,classes,options)
{
	return "<input style=\"" + style + "\"  id=\"" + id + "\" type=textfield class=\"amigaTextField " + classes + "\" " + options + " value=\"" + text + "\">";
}

amigaPasswordField=function amigaPasswordField(id,text,style,classes,options)
{
	return "<div style=\"" + style + "\"  ><input id=\"" + id + "\" type=password class=\"amigaTextField " + classes + "\" " + options + " value=\"" + text + "\"></div>";
}


amigaTick=function amigaTick(id,style,check,classes)
{
	if (check)
	{
		return "<div style=\"" + style + "\"  ><img height=15px width=15px src=/images/themes/Amiga/ticked.png id=\"" + id + "\" class=\"amigaTick " + classes + "\"></div>";
	} else
	{
		return "<div style=\"" + style + "\"  ><img height=15px width=15px src=/images/themes/Amiga/notick.png id=\"" + id + "\" class=\"amigaTick " + classes + "\"></div>";	
	}
}


function mouseUpsessionsManager(workbenchId)
{
	$("#" + workbenchId).find(".sessionManagerBox" ).show();
	$("#" + workbenchId).find(".sessionManagerBox" ).css("z-index",ztop);
}



function mouseUpoptionsMenu(workbenchId)
{
	$("#" + workbenchId).find(".optBox" ).show();
	$("#" + workbenchId).find(".sessionManagerBox" ).css("z-index",ztop);
}

function mouseUptimberwolf(workbenchId)
{
	$("#" + workbenchId).find(".timberWolfBox" ).show();
	$("#" + workbenchId).find(".timberWolfBox" ).css("z-index",ztop);
}


function getChecked(idElement,workbenchId)
{
	log($("#" + idElement));
	if ($("#" + workbenchId).find("." + idElement).attr("src") == "/images/themes/Amiga/ticked.png" )
		{
			return true;
		}else
		{
			return false;
		}
}

function mouseUpworkbench()
{
	log("currentTerminal=none");
	currentTerminal="none";
}

function loadSessions(workbench)
{
	for (i=0;i<currentUser.sessions.length;i++)
		{
		sessions.set(currentUser.sessions[i].sessionId,currentUser.sessions[i]);
		}
	displayListSession(workbench);
}

function setChecked(idElement,value)
{
	if ( value )
	{
		$("#" + idElement).attr('src', '/images/themes/Amiga/ticked.png');
	}else
	{
		$("#" + idElement).attr('src', '/images/themes/Amiga/notick.png');		
	}
}

function addAboutBox(workbench)
{
	workbenchId=workbench.replace("workbench","");
	$("#" + workbench)
	.append(
			
			amigaBox(workbench +"_aboutBox","About..",200,100,"left:100px;position:absolute;top:100px;","aboutBox noselect",
			amigaText("Coxifred (2019) domotic70@gmail.com","noselect","position:absolute;top:20px;left:320px")));
	$(".aboutBox" ).hide();
}

function addTimberWolf(workbench)
{
	$("#" + workbench)
	.append(
			
			amigaBox(workbench +"_timberWolf","TimberWolf",800,600,"right:100px;position:absolute;top:100px;","timberWolfBox noselect",
			amigaText("<u>F</u>ile&nbsp;&nbsp;&nbsp;","left:4px;","TimberMenu") +
			amigaText("<u>E</u>dit&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			amigaText("<u>V</u>iew&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			amigaText("Hi<u>s</u>tory&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			amigaText("<u>B</u>ookmarks&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			amigaText("<u>T</u>ools&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			amigaText("<u>H</u>elp&nbsp;&nbsp;&nbsp;","","TimberMenu") +
			
			amigaText("<button class=\"ui icon button\"><i class=\"arrow right icon\"></i></button>","position:absolute;top:48px;left:25px;border:1px","") +
			
			amigaText("<button class=\"large circular ui icon button\"><i class=\"icon arrow left\"></i></button>","position:absolute;top:45px","") +
			amigaText("<i class=\"camera retro icon\"></i>","position:absolute;top:55px;left:68px","")+
			amigaText("<i class=\"small angle down icon\"></i>","position:absolute;top:60px;left:90px","")+
			
			
			amigaTextField("","https://www.amigaimpact.org/","position:absolute;top:55px;left:110px;width:500px;box-sizing:border-box;background-color:white;border-radius:3px","urlBar")
			
			 +
			"<iframe id=" + workbench + "_timberWolf_frame sandbox=\"allow-scripts allow-forms allow-modals\"  style=position:absolute;left:-0px;top:85px;width:100%;height:500px src=\"https://www.amigaimpact.org\">"
			,"border:0px;")
			
			);
	
	resizeTimber($("#" + workbench + "_timberWolf"));
	$(".timberWolfBox" ).hide();
}

function resizeTimber(element)
{
	$("#" +element.attr("id") +"_frame").css("height",(element.css("height").replace("px","") - 130) + "px");
}

function addSessionManager(workbench)
{
	workbenchId=workbench.replace("workbench","");
	$("#" + workbench)
	.append(
			
			amigaBox(workbench +"_sessionManagerBox","SessionManager",600,600,"left:100px;position:absolute;top:100px;","sessionManagerBox noselect", 
				amigaList("listSession",300,400,"position:absolute;top:20px;left:7px","listSession") + 
				amigaButton("addSession","Add","width:100px;position:absolute;bottom:-460px;left:7px") + 
				amigaButton("applySession","Apply","width:100px;position:absolute;bottom:-460px;left:107px") + 
				amigaButton("deleteSession","Delete","width:100px;position:absolute;bottom:-460px;left:207px") + 
				amigaButton("cancelSession","Cancel","width:293px;position:absolute;bottom:-550px","HideableSessionManager") + 
				amigaButton("saveSession","Save","width:294px;position:absolute;bottom:-550px;left:293px","HideableSessionManager") +
				amigaText("Session Name","position:absolute;top:20px;left:320px") +
				amigaTextField("sessionId","","position:absolute;top:20px;left:410px","sessionId") +
				amigaText("Host","position:absolute;top:40px;left:320px") +
				amigaTextField("hostId","","position:absolute;top:40px;left:410px","hostId") + "<hr style=position:absolute;top:80px;left:320px;width:260px>" +
				amigaTick("showInTab","position:absolute;top:103px;left:380px",false,"showInTab") +
				amigaText("Always open in tab","position:absolute;top:100px;left:410px") +
				amigaTick("showWorkbench","position:absolute;top:123px;left:380px",false,"showWorkbench") +
				amigaText("Shortcut on workbench","position:absolute;top:120px;left:410px") +
				amigaTick("transparent","position:absolute;top:143px;left:380px",true,"transparent") +
				amigaText("Transparency","position:absolute;top:140px;left:410px") + "<hr style=position:absolute;top:170px;left:320px;width:260px>" +
				amigaTick("useCredential","position:absolute;top:190px;left:380px",true,"useCredential") +
				amigaText("Use current credentials","position:absolute;top:190px;left:410px") + 
				amigaText("Login Name","position:absolute;top:220px;left:320px") +
				amigaTextField("loginName","","position:absolute;top:220px;left:410px","loginName","disabled") + 
				amigaText("Password","position:absolute;top:240px;left:320px") +
				amigaPasswordField("password","","position:absolute;top:240px;left:410px","password","disabled") + "<hr style=position:absolute;top:270px;left:320px;width:260px>")
			);
	$(".sessionManagerBox" ).hide();
	
	
	
	
	
	$(".useCredential").click(function() {	
		var disabled=$('body').find(".loginName").prop("disabled");
		if (disabled )
			{
			$('body').find(".loginName").prop("disabled",false); 
			$('body').find(".password").prop("disabled",false); 
			}else
				{
				$('body').find(".loginName").prop("disabled",true);
				$('body').find(".loginName").val("");
				$('body').find(".password").prop("disabled",true);
				$('body').find(".password").val(""); 
				}
	});
	
	
}


function mouseUpapplySessionButton(workbenchId)
{
	mouseUpaddSessionButton(workbenchId);
}

function mouseUpaddSessionButton(workbenchId){
	if ( $("#" +workbenchId ).find(".sessionId").val() != "" && $("#" +workbenchId ).find(".hostId").val() != "" )
	{
		newSession={
			   "sessionId"     : $("#" +workbenchId ).find(".sessionId").val(),
			   "hostId"        : $("#" +workbenchId ).find(".hostId").val(),
			   "showInTab"     : getChecked("showInTab"),
			   "showWorkbench" : getChecked("showWorkbench"),
			   "transparent"   : getChecked("transparent"),
			   "useCredential" : getChecked("useCredential"),
			   "loginName"     : $("#" +workbenchId ).find(".loginName").val(),
			   "password"      : $("#" +workbenchId ).find(".password").val(),
		};
		sessions.set($("#" +workbenchId ).find(".sessionId").val(),newSession);
		log(newSession);
		loadSessions("workbench1");
		loadSessions("workbench2");
		loadSessions("workbench3");
		
	}
}

function loadSession(elementId){
		aSession=sessions.get(elementId);
		$('body').find(".sessionId").val(aSession.sessionId);
		$('body').find(".hostId").val(aSession.hostId);
		setChecked("showInTab",aSession.showInTab);
		setChecked("showWorkbench",aSession.showWorkbench);
		setChecked("transparent",aSession.transparent);
		setChecked("useCredential",aSession.useCredential);
		$('body').find(".loginName").val(aSession.loginName);
		$('body').find(".password").val(aSession.password);
}

function mouseUpsaveSessionButton(){
	getValueFromActionPost("admin/action=saveSessions",true,JSON.stringify(Array.from(sessions.values())),"saveSessions");
}

function displayListSession(workbenchId){
	$("#" + workbenchId).find(".listSession").html("");
	$("#" + workbenchId).find(".sessionList").html("");
	for (var aSession of sessions.values())
	{
		$("#" + workbenchId).find(".listSession").append("<div style=position:relative;z-index=" + ztop + " class=\"selectionable loadable\" id=\"" + aSession.sessionId + "\">" + aSession.sessionId + "@" + aSession.hostId +"</div>");
		$("#" + workbenchId).find(".sessionList").append("<div class=\"sessionable\" id=\"" + aSession.sessionId + "\">" + aSession.sessionId + "@" + aSession.hostId +"</div>");
	}
}

function displayMemory() {
	log(" * Display memory");
	try {
		var memory = performance.memory;
		usedHeap = numberWithCommas(memory.usedJSHeapSize);
		maxSizeHeap = numberWithCommas(memory.jsHeapSizeLimit);
		$('body').find(".memory").html(usedHeap + " Chip-RAM " + maxSizeHeap + " other RAM");
	} catch (e) {
		log(" * Could not retrieve memory this browser " + e);
	}

}

function loadUser()
{
	getValueFromUrl("admin?action=getMe",true,function(aJson){
		try
		{
			currentUser=JSON.parse(aJson);
			log(currentUser);
			userId=currentUser.name;
			initOrReload(currentUser.workbenchs.workbench1,"workbench1");
			initOrReload(currentUser.workbenchs.workbench2,"workbench2");
			initOrReload(currentUser.workbenchs.workbench3,"workbench3");
			
			registerEvent();
		} catch (e)
		{
			log("Error " + e);
			log(e);
		}
	});
}

function initOrReload(payload,workbenchId)
{
	if (payload.length > 0 && payload.indexOf("menuAmiga") > 0 && payload.indexOf("termContainer") > 0 )
	{
		$("#" +workbenchId ).html(payload);
		loadSessions(workbenchId);
		rebindTerm(workbenchId);
	}else
	{
		buildWorkbench(workbenchId);
	}
	
	id=workbenchId.replace("workbench","");
	$("#" + workbenchId).find(".layer").addClass("disabled");
	$("#" + workbenchId).find(".layer" + id).removeClass("disabled");

}


function buildWorkbench(workbench)
{
	//alert("Rebuild");
	log(" * Adding Menu in " + workbench);
	addContainer(workbench);
	
	addMenu(workbench);
	addContextMenu(workbench);
	
	log(" * Adding Session Manager Box in " +workbench);
	addSessionManager(workbench);
	
	log(" * Adding About Box in " +workbench);
	addAboutBox(workbench);
	
	log(" * Adding Timberwolf in " + workbench);
	addTimberWolf(workbench);

	log(" * Loading sessions in "+workbench);
	loadSessions(workbench);
}

log(" * Loading user");
loadUser();



function registerEvent()
{
	
	
										$('body').find(".sessionManagerBox").draggable({
									  		  cursor: '/cursors/Amiga/OS4.1.cur',
											  addClasses: false,
											  opacity: 0.35,
											  classes: {
												    "ui-draggable": "invisible"
												  }
										});
										
										$(".aboutBox").draggable({
									  		  cursor: '/cursors/Amiga/OS4.1.cur',
											  addClasses: false,
											  opacity: 0.35,
											  classes: {
												    "ui-draggable": "invisible"
												  }
										});
										
										
										$('body').find(".timberWolfBox").draggable({
									  		  cursor: '/cursors/Amiga/OS4.1.cur',
											  addClasses: false,
											  opacity: 0.35,
											  classes: {
												    "ui-draggable": "invisible"
												  }
										});
										
										 $('.timberWolfBox').removeClass("ui-resizable");
										 $('.timberWolfBox').find("div").remove(".ui-resizable-handle");
									
										 $('body').find(".timberWolfBox").resizable({
						                	helper: "ui-resizable-helper",
						                    ghost: true,
 											stop: function (event, ui) {
                        						resizeTimber($(this));
                    						}	
						                });

										/*
										 * Display Context Menu
										 * 
										 */
										$(".workbench").mousedown(function(e) {
											if (e.which == 3) {
												$('body').find(".menuAmiga").hide();
												$('body').find(".menuContextAmiga").show();
											}
										});
										
										
										
										/**
										 * Surlign with blue and deselect others
										 */
										$("body").delegate(".selectionable", 'click', function(){
											log("Click on selectionable");
											$(".selectionable").each(function(index) {
												$(this).css("background-color", "transparent").css("color", "black");
											});
											currentSelectedObject=$(this).attr("id");;
											$(this).css("background-color", "#3F69B4").css("color", "white");
										});
										
										$("body").delegate(".sessionable", 'mouseover', function(){
											$(".sessionable").each(function(index) {
												$(this).css("background-color", "transparent").css("color", "black");
											});
											$(this).css("background-color", "#3F69B4").css("color", "white");
										});
										
										
										
										$("body").delegate(".aboutMenu", 'mouseup', function(){
											$("#" + currentWorkbench).find(".aboutBox").show();
										});
										
										
										$("body").delegate(".quit", 'mouseup', function(){
										     getValueFromUrl("admin?action=logout",true,function(aJson){		
														setUrl("index.html");	
											 });				
										});
										
										$("body").delegate(".sessionable", 'mouseup', function(){
											id=$(this).attr("id");
											session=sessions.get(id);
											//$("body").css('cursor', 'pointer');
											try{
												var timeout = setTimeout(function() {
													launchTerm(session,currentWorkbench.replace("workbench",""));
											    }, 10);
											} catch (e)
											{
												alert(e);
											}
										});
										
										$("body").delegate(".sessionable", 'mouseout', function(){
											$(".sessionable").each(function(index) {
												$(this).css("background-color", "transparent").css("color", "black");
											});
										});
										
										$("body").delegate(".resetWorkbench", 'mouseout', function(){
											name=$(this).attr("name");
											
											getValueFromUrl("admin?action=resetFactoryWorkbench&workbench="+name,true,function(aJson){
												$("#" + name).html("");
												buildWorkbench(name);
												registerEvent();
											});
										});
										
										
										$("body").delegate(".loadable", 'click', function(){
											log("Click on loadble");
											currentSelectedObject=$(this).attr("id");
											loadSession(currentSelectedObject);
										});
										
										/**
										 * Surlign with blue
										 * 
										 * @returns
										 */
										$(".surlignable").hover(function() {
											id = $(this).attr("id");
											$(".surlignable").each(function(index) {
												$(this).css("background-color", "transparent").css("color", "black");
											});
											$(this).css("background-color", "#3F69B4").css("color", "white");
										}, function() {
											$(this).css("background-color", "transparent").css("color", "black");
										});
										
										/**
										 * When tick is clicked
										 */
										$(".amigaTick").click(function() {
											var src = ($(this).attr('src') === '/images/themes/Amiga/ticked.png')? '/images/themes/Amiga/notick.png' : '/images/themes/Amiga/ticked.png';
											$(this).attr('src', src);
										});
										
										/**
										 * When enter into Workbench Menu
										 * 
										 * @returns
										 */
										$(".workBenchMenu").mouseenter(function() {
											$('body').find(".workBenchMenuContext").show();
											$('body').find(".windowMenuContext").hide();
											$('body').find(".toolMenuContext").hide();
										});
										
										/*
										 * When enter into sessionMenu
										 */
										$(".sessionsMenu").mouseenter(function() {
											$('body').find(".sessionsMenuContext").show();
											$('body').find(".optionsMenuContext").hide();
										});
										
										/**
										 * When Enter into options Menu
										 * 
										 * @returns
										 */
										$(".optionsMenu").mouseenter(function() {
											$('body').find(".optionsMenuContext").show();
											$('body').find(".sessionsMenuContext").hide();
										});
										
										/**
										 * When enter into Quit entry
										 * 
										 * @returns
										 */
										$(".quit").mouseenter(function() {
											$('body').find(".sessionsMenuContext").hide();
											$('body').find(".optionsMenuContext").hide();
										});
										
										/**
										 * When Enter into aboutMenu
										 * 
										 * @returns
										 */
										$(".aboutMenu").mouseenter(function() {
											$('body').find(".sessionsMenuContext").hide();
											$('body').find(".optionsMenuContext").hide();
										});
										
										/**
										 * When Enter into Timberwolf
										 * 
										 * @returns
										 */
										$(".timberwolf").mouseenter(function() {
											$('body').find(".soundMenuContext").hide();
										});
										
										/**
										 * When enter into Theme menu
										 * 
										 * @returns
										 */
										$(".themeMenu").mouseenter(function() {
											$('body').find(".themeMenuContext").show();
											$('body').find(".soundMenuContext").hide();
										});
										
										$(".sessionsMenuContext").mouseleave(function() {
											$('body').find(".sessionsMenuContext").hide();
										});
										
										$(".optionsMenuContext").mouseleave(function() {
										$('body').find(".optionsMenuContext").hide();
										});
										
										/**
										 * When enter into music menu
										 * 
										 * @returns
										 */
										$(".musicMenu").mouseenter(function() {
											$('body').find(".soundMenuContext").show();
											$('body').find(".themeMenuContext").hide();
										});
										
										/**
										 * When enter into window menu
										 * 
										 * @returns
										 */
										$(".windowMenu").mouseenter(function() {
											$('body').find(".windowMenuContext").show();
											$('body').find(".workBenchMenuContext").hide();
											$('body').find(".sessionsMenuContext").hide();
											$('body').find(".optionsMenuContext").hide();
											$('body').find(".toolMenuContext").hide();
										});
										
										/**
										 * When enter into tool menu
										 * 
										 * @returns
										 */
										$(".toolMenu").mouseenter(function() {
											$('body').find(".toolMenuContext").show();
											$('body').find(".windowMenuContext").hide();
											$('body').find(".workBenchMenuContext").hide();
										});
										
										/**
										 * When release right button
										 * 
										 * @param e
										 * @returns
										 */
										$(".workbench").mouseup(function(e) {
											currentWorkbench=$(this).attr("id");
											
											if (e.which == 3) {
												$('body').find(".menuAmiga").show();
												$('body').find(".menuContextAmiga").hide();
												$('body').find(".workBenchMenuContext").hide();
												$('body').find(".sessionsMenuContext").hide();
												$('body').find(".optionsMenuContext").hide();
												$('body').find(".windowMenuContext").hide();
												$('body').find(".soundMenuContext").hide();
												$('body').find(".themeMenuContext").hide();
												$('body').find(".toolMenuContext").hide();
											}
											 var elementId = (e.target || e.srcElement).id;
											 if ( elementId != "" )
												 {
													 try{
														 var code = "mouseUp" + elementId +"(\"" + currentWorkbench + "\");";
														 eval(code);
													 }catch (e)
													 {
														log("No function mouseUp" + elementId +"(\"" + currentWorkbench + "\"); found " +e); 
													 }
												 }
										});
										
										
										
										$(".HideableSessionManager").click(function(e) {
											if (e.which == 1) {
												$('body').find(".sessionManagerBox").hide();
											}
										});
										
										$(".layer").click(function(e) {
											id=$(this).attr("name");
											displayWorkbench(id);
										});
										
										
										$(".amigaButton").mousedown(function(e) {
											if (e.which == 1) {
												$(this).addClass("amigaButtonPressed");
												$(this).parent().addClass("amigaButtonBackPressed");
												$(this).parent().parent().addClass("amigaButtonPerimeterPressed");
											}
										});
										$(".amigaButton").mouseup(function(e) {
											$(this).removeClass("amigaButtonPressed");
											$(this).parent().removeClass("amigaButtonBackPressed");
											$(this).parent().parent().removeClass("amigaButtonPerimeterPressed");
										
										});
	}
});