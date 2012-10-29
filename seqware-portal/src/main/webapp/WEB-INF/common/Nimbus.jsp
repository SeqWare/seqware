<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" errorPage="/WEB-INF/jsp/ErrorPage.jsp" %>
<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><tiles:getAsString name="title"/></title>
	
	<link rel="stylesheet" type="text/css" href="styles/reset.css" media="screen,projection" />
	<link rel="stylesheet" type="text/css" href="styles/main.css" media="screen,projection" />
	<!--link rel="stylesheet" type="text/css" href="styles/tabs.css" media="screen,projection" /-->
	<link rel="stylesheet" type="text/css" href="styles/treeview.css" media="screen,projection" />
	<link rel="stylesheet" type="text/css" href="styles/listview.css" media="screen,projection" />
	<link rel="stylesheet" type="text/css" href="styles/popup.css" media="all" />
   	<link rel="stylesheet" type="text/css" href="styles/fcbk.css"  media="screen" />
	<link rel="stylesheet" type="text/css" href="styles/report.css" media="screen,projection" />
	
    	<link rel="stylesheet" type="text/css" href="styles/container.css" />
	<link rel="stylesheet" type="text/css" href="styles/flexigrid.pack.css" />
	<link rel="stylesheet" type="text/css" href="styles/jqToolTip/jquery.tooltip.css" media="screen,projection" />
	
	<!--[if IE 7]><link rel="stylesheet" type="text/css" media="screen,projection" href="styles/ie7.css" /><![endif]-->
	<!--[if IE 8]><link rel="stylesheet" type="text/css" media="screen,projection" href="styles/ie8.css" /><![endif]-->
	
	
  <!--script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script-->
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
  <!--script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.2.6/jquery.min.js"></script-->
  <!--script type="text/javascript" src="scripts/jquery.js"></script-->
  <script type="text/javascript" src="scripts/jquery.cookie.js"></script>
  <script type="text/javascript" src="scripts/jquery.treeview.js"></script>
  <script type="text/javascript" src="scripts/jquery.treeview.async.js"></script>  
  <script language="javascript" src="scripts/common.js"></script>
  <script language="javascript" src="scripts/jquery.ui.core.js"></script>
  <script language="javascript" src="scripts/jquery.ui.widget.js"></script>
  <script language="javascript" src="scripts/jquery.ui.tabs.js"></script>
  <script language="javascript" src="scripts/supernote.js"></script>
  <script type="text/javascript" src="scripts/jquery-add-ons.js"></script>
  <script type="text/javascript" src="scripts/jquery.fcbkcomplete.js"></script>
  
  <script type="text/javascript" src="scripts/yahoo.js" ></script>
  <script type="text/javascript" src="scripts/event.js" ></script>
  <script type="text/javascript" src="scripts/dom.js" ></script>
  <script type="text/javascript" src="scripts/dragdrop.js" ></script>
  <script type="text/javascript" src="scripts/animation.js" ></script>
  <script type="text/javascript" src="scripts/container.js"></script>
  <script type="text/javascript" src="scripts/ResizePanel.js"></script>
	<!--Load the AJAX API-->
  <script type="text/javascript" src="https://www.google.com/jsapi"></script>

  <script type="text/javascript" src="scripts/flexigrid.js" />
  <script src="http://cdn.jquerytools.org/1.2.6/jquery.tools.min.js"></script>

  <script type="text/javascript" src="scripts/jquery.tooltip.min.js"></script>


  
  <script type="text/javascript">	  

	YAHOO.namespace("index.resize");
	function init() {
		var width = (0.8*$(window).width()).toFixed() +"px";
		var height = (0.9*$(window).height()).toFixed() +"px";
		YAHOO.index.resize.panel = new YAHOO.widget.ResizePanel("win", 
			{ 
				effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25}, 
				width: width,
				height: height,
				fixedcenter: true,
				constraintoviewport: true,
				underlay:"shadow",
				close:true,
				visible:false,
				draggable:true,
				modal:false 
			} 
		);
		YAHOO.index.resize.panel.render();
	}
	YAHOO.util.Event.addListener(window, "load", init);

  function getRandomInt() { return Math.random()*1000; };

  // open Node By 
  function openNodeById(){
    var nodeId = getCurrentNodeId();
    var nodeSelector = "span#" + nodeId;

	if(nodeId != ""){
	    var span = $(nodeSelector); 
	    while(span[0]){
		   	span.parent().children("ul").attr("style", "");
			span.parent("li").removeClass("expandable").addClass("collapsable");
			span = span.parent("li").parent("ul").parent("li").children("span:first");
	    }
	}
  }

  function isLaunchPage(){
	  var isLaunchPage = false;
	  if($("#typebulktree").attr("type-bulk-tree") == "launch"){
		  isLaunchPage = true;
	  }
	  return isLaunchPage;
  }

  function getWorkflowParamList(workflowId){
	$.ajax({
	    url: "<c:url value="workflowParamList.htm"/>?key=" + getRandomInt(),     
	    data: {workflowId: workflowId},
	    dataType: "html",                      
	    success: function (response) {
	    	var result = eval(response).workflowParams;
		    
		    // errors block
			if(result[0].isHasError){
				//errorText = "The selected " + response[0].workflowFullName + " workflow has no associated parameters and input file types. This workflow cannot be launched. Please select another workflow";
				$("div.userError").append(result[0].errorMessage);
			}

		    // view workflow description
	    	$("#choose-workflow-desc").empty().html("<label>" + result[0].workflowDesc + "</label><br/><br/>");
	
	    	// view workflows params with values
			$("#dynamic-params").empty();
			for(i=1; i < result.length; i++){
				var item = result[i];
				if(item.display == "true"){
					var html = "";					

					if(item.type=="pulldown"){
						// one record workflow_param_value
						if(item.values.length == 0){
							html ="<label>" + item.displayName + "</label> <input type='text' class='m-txt' name='"+ item.id +"' value='"+ item.defaultValue +"'/>";
						}else
						if(item.values.length == 1){
							html ="<label>" + item.displayName  + "</label> <input type='text' class='m-txt' name='"+ item.id +"' value='"+ item.values[0].displayName +"'/>";
						}else{
							var listHtml = "";//"<option selected value='defaultValue'>"+ item.defaultValue +"</option>"
				
							for(j=0; j<item.values.length; j++){
								var selected = "";
								if(item.defaultValue == item.values[j].value){
									selected = "selected";
								}
							
								listHtml = listHtml + "<option value='"+ item.values[j].id +"' "+ selected+">"
								+ item.values[j].displayName + "</option>";
							}
							html = "<label>" + item.displayName + "</label> <select id='"+item.id +"' name='"+ item.id +"'>" + listHtml +"</select>"; 
						}
					}else{
						html ="<label>" + item.displayName + "</label> <input type='text' class='m-txt' name='"+ item.id +"' value='"+ item.defaultValue +"'/>";
					}
					$("#dynamic-params").append(html);
				}	
			}
	    },
		error:function (xhr, error/*ajaxOptions, thrownError*/){
		    showError(xhr, error, $("div.userError"));
        } 
	});
  }

  var COUNT_LOAD_PROCESS = 0;
  var IS_LOAD_FILES_FINISHED = false;
  var IS_LOAD_TREE_FINISHED = false;
//var IS_OPEN_INDEX_PAGE = false;
  var IS_STOP_LOAD_INDEX_PAGE = false;
  var IS_LOAD_INDEX_PAGE = false;

  var DOWNLOAD_REPORT_BUNDLE_XHR = null;

  var IS_LOAD_POPUP = false;
  var COUNT_NAV_OPER = 0;

  function abortLoadingReportBundle(){
	  if(DOWNLOAD_REPORT_BUNDLE_XHR != null){
	  	DOWNLOAD_REPORT_BUNDLE_XHR.abort();
	  }
  }

  function getCountNavOper(){ return COUNT_NAV_OPER; }
  function incrementCountNavOper(){ COUNT_NAV_OPER++; }
  function decreaseCountNavOper(){ 
	  if(COUNT_NAV_OPER > 0){
		  COUNT_NAV_OPER--;
	  }
  }

  function getCountLoadProcess(){ return COUNT_LOAD_PROCESS;}
  function incrementCountLoadProcess(){ COUNT_LOAD_PROCESS++; }
  function decreaseCountLoadProcess(){
	  if(COUNT_LOAD_PROCESS > 0){
	  	COUNT_LOAD_PROCESS--;
	  }
  }

  function startTimeOut(){
	return setTimeout(showHideSpinner, 1000);
  }

  function showHideSpinner() {
	if (COUNT_LOAD_PROCESS > 0) {
		$("#download-spinner").show();
	} else {
		$("#download-spinner").hide();
	}
  }

  function startTimeOutOpeningReportBundle(){
	return setTimeout(function() {
		$("#opening-report-bundle-popup").togglePopup();
		$("#download-spinner").show(); 
		}, 1000);
  }

  function showError(x, e, elem){
  	var errorMessage = "";
	if(x.status==0){
		errorMessage = "You are offline. Please Check Your Network.";
	}else if(x.status==404){
		errorMessage = "Requested URL not found.";
	}else if(x.status==500){
		errorMessage = "Internel Server Error.";
	}else if(e=='parsererror'){
		errorMessage = "Error.Parsing JSON Request failed.";
	}else if(e=='timeout'){
		errorMessage = "Request Time out.";
	}else {
		errorMessage = "Unknow Error."+x.responseText;
	}

	if(errorMessage!=""){
		elem.empty().append(errorMessage);
	}
  }

  $(document).ready(function(){
	// show selected download files
	url = "<c:url value="downloadFileList.htm"/>" + "?key=" + getRandomInt();
	loadSelectedDownload(url, false, startTimeOut());

	// show selected inputs
	url = "<c:url value="selectInputList.htm"/>" + "?key=" + getRandomInt();
	loadSelectedDownload(url, false, startTimeOut());
		
	// select workflow
	$("select#workflows").bind('change', function(){ 
		$("div.userError").empty();
		getWorkflowParamList($('select#workflows').val());

		// refresh selected input
		url = "<c:url value="selectInputList.htm"/>" + "?key=" + getRandomInt();
		loadSelectedDownload(url, false, startTimeOut());
	});

    // submit forms by clicking to the link
	$("a[typesubmit]").bind('click', function(){ 
		var typeSubmit = $(this).attr('typesubmit');
		$("#hidden_submit").attr('name', typeSubmit);
		$(this).closest('form').submit();
		return false;
	});
	// open/close sidebar
	$("div.b-expander").bind('click', function(){
		$(this).closest("div.b-news").toggleClass("m-collapsed");
	});  
	// select link
	$("div.b-news-content > ul > li > a").bind('click', function(){
 		var classNameChooseLink = "m-current";			
		if (!$(this).hasClass(classNameChooseLink)){
			$("div.b-news-content > ul > li > a").removeClass(classNameChooseLink);
			$(this).addClass(classNameChooseLink);
		}
	});

	// tree navigation
	$("a[operation='tree-action']").bind('click', function(){
		var root = $(this).closest(".b-content-paging");
		var id = $(root).attr('tree-id');
		var action = $(this).attr('operation-type');
		
		var asc;
		if(action=="sorting"){
			asc = $(this).attr('val');
			if(asc == "true"){
				asc = "false";
			}else{
				asc = "true";
			}
			$(this).attr('val', asc)

			if(asc == "true"){
				$(this).empty().append($(this).attr('t-val'));				 				 
			}else {
				$(this).empty().append($(this).attr('f-val'));
			}
		}
		var type_class = $("#root-" + id).find("> ul").attr("class");
		$("#root-" + id).empty().append("<ul id='" + id + "' class='" + type_class + "'></ul>");
		var ajaxUrl = $('#root-' + id).attr('ajax-url');
		var typeList = $('#root-' + id).attr('type-list');
		 
		IS_LOAD_FILES_FINISHED = true;
		$("#" + id).treeview({
		  	collapsed: true,
		   	url: ajaxUrl +"?action=" + action + "&typeList=" + typeList + "&asc=" + asc + "&key=" + getRandomInt(),
		   	spinner: $("#download-spinner"),
			treeId: id
		});
	});
	
	$("select[name='page_size']").bind('change', function(){
		var root = $(this).closest("div.b-content-paging");
		var id = $(root).attr('tree-id');
		var itemsPage = $(root).parent().find('select[name="page_size"] option:selected').val();
	
		var type_class = $("#root-" + id).find("> ul").attr("class");
		$("#root-" + id).empty().append("<ul id='" + id + "' class='" + type_class + "'></ul>");
	
		var ajaxUrl = $('#root-' + id).attr('ajax-url');
		var typeList = $('#root-' + id).attr('type-list');
			 
		$("#" + id).treeview({
			collapsed: true,
		   	url: ajaxUrl +"?typeList=" + typeList + "&key=" + getRandomInt() + "&pi=" + itemsPage,
		   	spinner: $("#download-spinner"),
			treeId: id
		});
	});

	// search action
	$("a[operation='search-action']").bind('click', function(){
		var root = $(this).closest(".b-content-paging");
		var id = $(root).attr('tree-id');
		var action = $(this).attr('operation-type');

		var type_class = $("#root-" + id).find("> ul").attr("class");
		$("#root-" + id).empty().append("<ul id='" + id + "' class='" + type_class + "'></ul>");
		var ajaxUrl = $('#root-' + id).attr('ajax-url');
		var type = $('#type').val();
		var criteria = $('#criteria').val();
	
		
		console.log($('#casesens'));
		console.log(isCaseSens);
		IS_LOAD_FILES_FINISHED = true;
		$("#" + id).treeview({
		  	collapsed: true,
		   	url: ajaxUrl +"?action=" + action + "&type=" + type + "&criteria=" + criteria + "&key=" + getRandomInt(),
		   	spinner: $("#download-spinner"),
			treeId: id
		});
	});

	// search button action
	$("#searchbutton").bind('click', function(){
		
		$('#issearched').val(true);
		var ajaxUrl = $('#ajaxurl').val();
		var type = $('#type').val();
		var criteria = $('#criteria').val();
		console.log($('#casesens'));
		var isCaseSens = $('#casesens')[0].checked == true;
		 
		IS_LOAD_FILES_FINISHED = true;
		$.ajax({
		  	url: ajaxUrl +"?mode=create&type=" + type + "&criteria=" + criteria + "&casesens=" + isCaseSens.toString() + "&key=" + getRandomInt(),
		   	success: function(data){
				$('#searcharea').html(data);
			}
		});
	});

	// disable/undisable paired-file
	$("#upload-files input[type='checkbox']").bind('click', function(){ 
		var checked = $(this).attr('checked');
		var inputSingleFile = $(this).prev("input");
		var inputPairedFile = $(this).next("input");
		if(checked){
			$(inputSingleFile).attr('disabled','disabled');
			$(inputPairedFile).removeAttr('disabled');	
		}else{
			$(inputSingleFile).removeAttr('disabled');
			$(inputPairedFile).attr('disabled','disabled');	
		}
	 });

	// click to use URL for file upload
	$("#use-url-for-file-upload").bind('click', function(){
		var checked = $(this).attr('checked');
		var browserFile = $("#browser-upload-file input");
		var checkPairedFile = $("#browser-upload-file input[name='paired']");
		var inputPairedFile = $("#browser-upload-file input[paired-file]");
		var urlFile = $("#url-upload-file input");
		if(checked){
			browserFile.attr('disabled','disabled');
			urlFile.removeAttr('disabled')
			//urlFile.attr('disabled','');	
			checkPairedFile.attr('checked','');
		}else{
			browserFile.attr('disabled','');
			browserFile.removeAttr('disabled');
			urlFile.attr('disabled','disabled');
			checkPairedFile.attr('checked','');
			inputPairedFile.attr('disabled','disabled');
		}
	});

	function getRootLi(currentLi){
		var rootLi;
		while(currentLi[0]){
			rootLi = currentLi;
			currentLi = currentLi.parent("ul").parent("li");
	    }
	    return rootLi;
	}

	function getListParentId(currentLi){
		var listParentId = "";
		while(currentLi[0]){
			listParentId = listParentId + currentLi.children("span:first").attr("id") + ",";
			currentLi = currentLi.parent("ul").parent("li");
		}
		return listParentId.slice(0, -1);
	}

	$("a[sn='y']").live('click', function(){
		var typeTree = $(this).attr('tt');
		if(typeTree == null){
			typeTree = $(this).closest("span").children("a[popup-delete='true']").attr('tt');
		}
		var listNodeId = getListParentId($(this).closest('li'));
		$.ajax({
		    async: false,
		    type: 'POST',
		    url: "<c:url value="saveOpenNode.htm"/>?key=" + getRandomInt(),     
		    data: {tt: typeTree, listNodeId: listNodeId},                      
		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			    showError(xhr, error, $("#tree-error"));
           	}   
		});
		return true;
	});

	// delete temp files on server
	function deleteTempFilesOnServer(){
		$.ajax({
		    type: 'GET',
		    url: "<c:url value="deleteLoadedIndexPage.htm"/>?key=" + getRandomInt(),
		    success: function () {},
		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			    showError(xhr, error, $("#tree-error"));
		    }
		});
	}
	
	// download file from server
	function downloadFile(fileId){
		  var url = "downloader.htm?fileId=" +fileId;
		  var elemIF = document.createElement("iframe");
		  elemIF.src = url;
		  elemIF.style.display = "none";
		  document.body.appendChild(elemIF);
	}

	function stopLoadPage(){
		$.ajax({ async: false,  type: 'GET',
		    url: "<c:url value="abortLoadIndexPage.htm"/>?key=" + getRandomInt(),    		      
		    success: function (response) {return true;}
		});
	}

	function activePopupStopLink(){
		var stopLink = $(".b-popup-browse .m-stop");
		stopLink.removeClass('m-disable');
//		stopLink.live('click', function(){
//			stopLoadPage();
//			return  false;
//		});
	}

	function inactivePopupStopLink(link){
		var stopLink = $(".b-popup-browse .m-stop");
		stopLink.addClass('m-disable');
		stopLink.die('click');
	}
	
	function activePopupNavLink(link){
		link.removeClass("m-disable");
		link.live('click', function(){
			var action = $(this).attr('action');
			loadPageFromZip("", "", false, action);
			return false;
		});
	}

	function inactivePopupNavLink(link){
		link.addClass("m-disable");
		link.die('click');
	}

	// handler click on Close popup
	$("div[close-popup]").live('click', function(){
		if(!$('#popup').hasClass('hidden')){
			var cancelOpeningReportBundleLink = $('#popup #cancel-openning-report-bundle');
				if(cancelOpeningReportBundleLink[0]){
					cancelOpeningReportBundleLink.click();
				}else{
					$('').togglePopup(); 
				}
		}
		return false;
	});

	$(window).keypress(function(e) {
		if(!$('#popup').hasClass('hidden')){
		    if(e.keyCode == 13) {
				var downloadReportBundleLink = $('#popup #yes-download-report');
				if(downloadReportBundleLink[0]){
					downloadReportBundleLink.click();
				}
				var okErrorReportBundleLink = $('#popup #error-opening-report-bundle-ok');
				if(okErrorReportBundleLink[0]){
					okErrorReportBundleLink.click();
				}

				var deleteObjLink = $('#popup #delete-obj');
				if(deleteObjLink[0]){
					onDelete(deleteObjLink);
				}
				return false;
		    }
		    // esc
		    if(e.keyCode == 27) {
				var cancelOpeningReportBundleLink = $('#popup #cancel-openning-report-bundle');
				if(cancelOpeningReportBundleLink[0]){
					cancelOpeningReportBundleLink.click();
				}else{
					$('').togglePopup(); 
				}
			}
		}
	});

	function stopLoadingReportBundle(){
		// stop request
		abortLoadingReportBundle();
		// hide spinner
		decreaseCountLoadProcess();
		if(getCountLoadProcess() == 0){
			$("#download-spinner").hide();
		}
		$("opening-report-bundle-popup").togglePopup();

		// active link show report-bundle
		IS_LOAD_POPUP=false;
		doClickOnZipFile();
	}

	// handler click 'Cancel' on OpenningReportBundle Popup
	$("#cancel-openning-report-bundle").live('click', function(){
		stopLoadingReportBundle();
		return false;
	});

	$(".panel  .close").live('click', function(){
		$(".panel > .bd").empty();
	});
	
	// handler click 'Yes' for Download Zip file
	$("#yes-download-report").live('click', function(){	
		// stop request
		abortLoadingReportBundle();
		// close popup
		$("#stop-popup").togglePopup();
		downloadFile( $(this).attr('fileId') );
		return false;
	});
		
	// handler click 'No' for Display Zip file
	$("#no-download-report").live('click', function(){
		var fileId = $("#no-download-report").attr('fileId', fileId);
		$("#stop-popup").togglePopup();
		loadPageFromZip(fileId, "", true, "", false);
		return false;
	});

	doClickOnZipFile();
	// handler click on the zip resource report file
	function doClickOnZipFile(){
		// handler click on the zip resource report file
		$("a[ft='z-r-b']").live('click', function() {
			$("a[ft='z-r-b']").die('click');
			var fileId = $(this).attr('file-id');
			$("#yes-download-report").attr('fileId', fileId);
			$("#no-download-report").attr('fileId', fileId);
			loadPageFromZip(fileId, "", true, "", true);
			return false;
		});
	}

	$(".panel .bd a").live('click', function(){
	//	if(!IS_LOAD_POPUP)
		$(".panel > .bd").empty()
		var href = $(this).attr('href');
		loadPageFromZip("", href, false, "", true);
		return  false;
	});		

	function loadPageFromZip(fileId, href, isZipFile, action, isCanAborted){
		if(!IS_LOAD_POPUP)
		{	
		IS_LOAD_POPUP = true;

		// inactive all navigation link
		inactivePopupNavLink($(".b-popup-browse .m-prev"));
		inactivePopupNavLink($(".b-popup-browse .m-next"));
		inactivePopupNavLink($(".b-popup-browse .m-home"));
		inactivePopupNavLink($(".b-popup-browse .m-reload"));

		// active Stop link
		activePopupStopLink();

		var timeoutIdOpenZip;
		if(isZipFile){
			if(!$('#popup').hasClass('hidden')){
				$("opening-report-bundle-popup").togglePopup();
			}
			timeoutIdOpenZip = startTimeOutOpeningReportBundle();
		}else{
		// start process which show spinner after 1 sec
	 		timeoutIdOpenZip = startTimeOut();
		}
		// increment COUNT_LOAD
		incrementCountLoadProcess();

//		var timeoutIdShowPopup;
//		if(fileId != ""){
//			var timeWait = getTimeOutLoadIndexPage();
//			timeoutIdShowPopup = setTimeout(function() { $("#download-spinner").hide(); $("#stop-popup").togglePopup(); }, timeWait);
//		}

		$("#tree-error").empty();
		DOWNLOAD_REPORT_BUNDLE_XHR = $.ajax({
		    type: 'GET',
		    url: "<c:url value="loadIndexPage.htm"/>?key=" + getRandomInt(),    
		    data: {isZipFile: isZipFile, fileId: fileId, href: href, action: action, isCanAborted: isCanAborted}, 
		    dataType: "html",                
		    success: function (response) {
		    	// dont show Stop view index page window
		    	if(isZipFile){
//		    		clearTimeout(timeoutIdShowPopup);
//		    		if(!$('#popup').hasClass('hidden')){
//			    		$('#stop-popup').togglePopup();
//		    		}
		    		doClickOnZipFile();
		    	}
			var result = eval(response).result[0];
			if(result.isStartPage == true){
				inactivePopupNavLink($(".b-popup-browse .m-prev"));
				inactivePopupNavLink($(".b-popup-browse .m-home"));
			}else{
				activePopupNavLink($(".b-popup-browse .m-prev"));
				activePopupNavLink($(".b-popup-browse .m-home"));
			}
			if(result.isEndPage == true){
				inactivePopupNavLink($(".b-popup-browse .m-next"));
			}else{
				activePopupNavLink($(".b-popup-browse .m-next"));
			}
			activePopupNavLink($(".b-popup-browse .m-reload"));
			inactivePopupStopLink();

		    // hide spinner if need
		    clearTimeout(timeoutIdOpenZip);	
			decreaseCountLoadProcess();
			if(getCountLoadProcess() == 0){
				$("#download-spinner").hide();
				if(isZipFile){
					if(!$('#popup').hasClass('hidden')){
						$("opening-report-bundle-popup").togglePopup();
					}
				}
			}

			IS_LOAD_POPUP = false;

			if(result.isHasError){
				$("#name-error-report-bundle").empty().append(result.zipFileName);
				$("#error-opening-report-bundle-popup").togglePopup();
			}else
			if(result.isAborted){
			//	$("#name-report-bundle").empty().append(result.zipFileName);
				$("#.bug-inner span").empty().append(result.zipFileName);
				$("#stop-popup").togglePopup();
				$("#list-entity").empty().append(result.html);
			}else{
			    // update index page content	
				if(isZipFile){
					YAHOO.index.resize.panel.show();
					var height = (0.9*$(window).height() - 50 - 33).toFixed() +"px";
					$(".panel .bd").css('max-height', height);
				}
				$(".panel > .bd").empty().append(result.html);
			}

		    },
		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			if (xhr.status != 0) {
			    showError(xhr, error, $("#tree-error"));
			}
            }   
		});
		}
	}
	
	// set action in the Delete popup window
	$("a[popup-delete='true']").live('click', function(){ 
		var formAction = $(this).attr('form-action');
		var objectId = $(this).attr('object-id');
		var objectName = $(this).attr('object-name');

		var saveNode = $(this).attr('sn'); 

		if(saveNode == "n"){
	    	$("#delete-popup #hidden-delete-object-id").attr('value', objectId);
			$("#delete-popup span:first").html(objectName);
			$("#delete-popup form").attr('action', formAction);
			$("#delete-popup").togglePopup(); 
		} else {
			var typeTree = $(this).attr('tt');
			var listNodeId = getListParentId($(this).closest('li'));
			
			$("#tree-error").empty();
			$.ajax({
			    type: 'POST',
			    url: "<c:url value="saveOpenNode.htm"/>?key=" + getRandomInt(),     
			    data: {tt: typeTree, listNodeId: listNodeId},                 
			    success: function () {
			    	$("#delete-popup #hidden-delete-object-id").attr('value', objectId);
					$("#delete-popup #hidden-type-tree").attr('value', typeTree);
					$("#delete-popup span:first").html(objectName);
					$("#delete-popup form").attr('action', formAction);
					$("#delete-popup").togglePopup(); 
			    },
			    error:function (xhr, error/*ajaxOptions, thrownError*/){
				    showError(xhr, error, $("#tree-error"));
	            }   
			});
		}
	
		return false;
	});

	// set action in the Delete popup window
	$("a[popup-cancel='true']").live('click', function(){ 
		var objectId = $(this).attr('object-id');
			
		var typeTree = $(this).attr('tt');
		var rootId = "";
		if(typeTree=="wfr" || typeTree=="wfrr"){
				rootId = getRootLi($(this).closest("span").parent("li")).attr("id");
		}

		var listNodeId = getListParentId($(this).closest('li'));
			
		var linkCancel = $(this);
		var spanWithTitle = $(this).parent("span").parent("li").children("span:first");
			
		//	var openNodeId = "";
		//	var isRoot = $(this).parent("span").parent("li").attr('root');
		//	if(isRoot = true){
		var	openNodeId = spanWithTitle.attr("id");
		//	}else{
		//		openNodeId = $(this).parent("span").parent("li").children("ul").children("li").attr("id");
		//	}

	    $("#tree-error").empty();
		$.ajax({
	//	    url: "<c:url value="setOpenNode.htm"/>?key=" + getRandomInt(),     
	//	    data: {tt: typeTree, rootId: rootId, openNodeId: openNodeId},  
			type: 'POST',
		    url: "<c:url value="saveOpenNode.htm"/>?key=" + getRandomInt(),     
		    data: {tt: typeTree, listNodeId: listNodeId}, 
		                        
		    success: function () {
			    $.ajax({
				    url: "<c:url value="cancelAnalysisWorkflow.htm"/>?key=" + getRandomInt(),     
				    data: {objectId: objectId},                      
				    success: function () {
					    var titles = $("span#"+openNodeId);
					    for(var i = 0; i < titles.length; i++){
					        var title = $(titles[i]).html();
					        var endIndex = title.lastIndexOf("(");
					        if(endIndex == -1) endIndex = title.length;
					        title = title.substring(0, endIndex) + "(cancelled) ";
					        $(titles[i]).html(title);
					    }
				    },
				    error:function (xhr, error/*ajaxOptions, thrownError*/){
					    showError(xhr, error, $("#tree-error"));
		            }
			    });
		    }
		});
		return false;
	});

	 // set action in the stderr popup window
	$("a[popup-stderr='true']").live('click', function(){ 
		var stdErr = $(this).attr('stderr');
    	        $("#stderr-popup textarea#stdTA").text(stdErr);
                $("#stderr-popup").togglePopup();
		return false;
	});

	 // set action in the stderr popup window
	$("a[popup-stdout='true']").live('click', function(){ 
		var stdOut = $(this).attr('stdout');
    	        $("#stderr-popup textarea#stdTA").text(stdOut);
                $("#stderr-popup").togglePopup();
		return false;
	});

	 // set action in the Share popup window
	$("a[popup-share='true']").live('click', function(){ 
		var formAction = $(this).attr('form-action');
		var objectId = $(this).attr('object-id');
		var objectName = $(this).attr('object-name');
 		var typeTree = $(this).attr('tt');
		var openNodeId = $(this).parent("span").parent("li").children("span:first").attr("id");
		var listNodeId = getListParentId($(this).closest('li'));
		// set params into Share form
    	$("#share-popup #hidden-share-open-node-id").attr('value', openNodeId);

		$("#tree-error").empty();
		$.ajax({
		    type: 'POST',
		    url: "<c:url value="saveOpenNode.htm"/>?key=" + getRandomInt(),     
		    data: {tt: typeTree, listNodeId: listNodeId},       
		    success: function () {
		    	$("#share-popup #hidden-share-object-id").attr('value', objectId);
				$("#share-popup span:first").html(objectName);
				$("#share-popup form").attr('action', formAction);
				$("#share-popup").togglePopup(); 
			    $("#emails").fcbkcomplete({
				    addontab: true,                   
				    height: 2,
			    	cache: true,
			    	filter_case: true,
			    	filter_hide: true,
			    	newel: true                    
				});
		    },
		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			    showError(xhr, error, $("#tree-error"));
            }
		});
		return false;
	});

	// selection tree node 
	$("a[selector='true']").live('click', function(){
		// star process which show spinner after 1 sec
	 	var timeoutIdGDF = startTimeOut();
		// increment COUNT_LOAD
		incrementCountLoadProcess();
		
		// open Download bar 
		if(isLaunchPage()){
			if($("#selected-input-bar").hasClass("m-collapsed")){
				$("#selected-input-bar .b-expander").click();
			}
		}else{
			var isCollapsed = $("#selected-download-bar").hasClass("m-collapsed");
			if(isCollapsed){
				$("#selected-download-bar .b-expander").click();
			}
		}

		var isSelect = false;
		// get current params
		var valueClass = '';
		var text = "select";
		
		if('select' == $(this).html()){
			text = "unselect";
			valueClass = "m-unselect";
			isSelect = true;
		}

		$(this).attr('class', valueClass).html(text);

		var root = $(this).parent("span").parent("li");
		$(root).find("ul > li > span > a[selector]").check(valueClass, text);
		
		// get files info
		var del = ",";
		// get all select node
//		var allIds = "";
//		var allStatuses = "";
		
		// check root nodes
		var currentNode = $(root); 
   		while(currentNode[0]){
		    var links = currentNode.parent().children("li").children("span").children("a[selector]");
			var selectedNode = links;
			
		/*	for(var i=0; i<selectedNode.length; i++) {
				var id = $(selectedNode[i]).parent().prev().attr("id");
				var statusText = $(selectedNode[i]).html();

				var st = 1;
				if(statusText == "select")
					st = 0;

				allIds = allIds + id + del;
				allStatuses = allStatuses + st + del;
			}
		*/
			// change selectors links
			var isChange = true;
		      	for( var i=0; i < links.length; i++){
				if(text != $(links[i]).html()){
					isChange = false;
				}
	      	}

			currentNode = currentNode.parent("ul").parent("li");
		
			if(isChange && text=="select"){
				var linkUp = currentNode.children("span").children("a[selector]");
				$(linkUp).attr('class', valueClass).html(text);
			}
   		}
		// delete ending symbol ','
//		allIds = allIds.slice(0, -1);
//		allStatuses = allStatuses.slice(0, -1);
		   		
		var url="";
		// if page Selected Input
		if(isLaunchPage()){
			url = "<c:url value="selectInputList.htm"/>" + "?key=" + getRandomInt();
		}else{
			url = "<c:url value="downloadFileList.htm"/>" + "?key=" + getRandomInt();
		}
		////////////////////////
		var link = this;
		var type = $(link).attr('file-sel-type');
		var nodeId =  $(link).attr('file-sel-id');
		
		var childId = "";
		if($(root).closest("li").children("span:first").attr('root') != "true"){
			childId = $(root).children("ul").children("li").attr("id");
		}
		
		// get type bulk tree
		var typeBulkTree = $("#typebulktree").attr("type-bulk-tree");
		$("#tree-error").empty();
		$.ajax({
			type: 'POST',
			url: url,
			data: { option: "updateFileList", typeNode: type,/*allSelectIds: allIds, allSelectStatuses: allStatuses,*/
					isSelect: isSelect, nodeId: nodeId, childId: childId, typeBulkTree: typeBulkTree},
	  		dataType: "json",
			success:  function(response) {
				clearTimeout(timeoutIdGDF);	

				decreaseCountLoadProcess();
				if(getCountLoadProcess() == 0){
					$("#download-spinner").hide();
				}
				viewResult(response, url);
			},
			error:function (xhr, error/*ajaxOptions, thrownError*/){
				showError(xhr, error, $("#tree-error"));
				clearTimeout(timeoutIdGDF);	
				decreaseCountLoadProcess();
	        }
		 });
	//	getDownloadFiles(url, isSelect, this);
	});

	 // cancel download files
	$("#cancel-download").bind('click', function(){
		$("#tree-error").empty(); 
		$.ajax({
			url: "<c:url value="cancelBulkDownloader.htm"/>" + "?key=" + getRandomInt(),                           
			success: function () {
				$("#study-file").empty();
				$("#page-info-file").empty();
			    $("a[selector]").html("select").attr('class', '');
			    doInactiveLink($("a[operation='file-list-action']"));
			    doInactiveStartDonwnloadLink();
			},
		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			    showError(xhr, error, $("#tree-error"));
            }
		});
	});

  });

  function getDownloadFiles(url, isSelect, link){
	// star process which show spinner after 1 sec
	var timeoutIdGDF = setTimeout(function() {$("#download-spinner").show(); }, 1000);
	  
	var type = $(link).attr('file-sel-type');
	var nodeId =  $(link).attr('file-sel-id');
	
	// get files info
	var del = ",";
	// get all select node
	// class='m-unselect' href='#' selector='true'
	var allIds = "";
	var allStatuses = "";
	var selectedNode = $("a[selector='true']");
	for(var i=0; i<selectedNode.length; i++) {
		var id = $(selectedNode[i]).parent().prev().attr("id");
		var statusText = $(selectedNode[i]).html();

		var st = 1;
		if(statusText == "select")
			st = 0;
		
		if(i == selectedNode.length - 1){
			allIds = allIds + id;
			allStatuses = allStatuses + st;
		}else{
			allIds = allIds + id + del;
			allStatuses = allStatuses + st + del;
		}
	}

	// get type bulk tree
	var typeBulkTree = $("#typebulktree").attr("type-bulk-tree");
	 
	$.post(url, { option: "updateFileList", typeNode: type, /*ids: ids, statuses: statuses, */
		 				allSelectIds: allIds, allSelectStatuses: allStatuses, isSelect: isSelect,
		 				nodeId: nodeId, typeBulkTree: typeBulkTree}, 
		function(response) {
			clearTimeout(timeoutIdGDF);	
			decreaseCountLoadProcess();
			if(getCountLoadProcess() == 0){
				$("#download-spinner").hide();
			}
	
			viewResult(response, url);
			
		}, "json");
	}

	function loadSelectedDownload(url, isPaginationAction, timeoutId){

//	 if(isPaginationAction){
	 	incrementCountLoadProcess();
//	 }
		$("#tree-error").empty();
		$.ajax({
			type: 'POST',
		 	url: url,
		 	data: { option: "getCurrentFileList"},
		 	dataType: "json",
		 	success:  function(response) {
			 	clearTimeout(timeoutId);

			 	// add displayName in Selected Input bar if want
			 	if(response[0].isSelectedInput == true && isLaunchPage()){
				 	$("#workflow-param-display-name").empty();
					if (response[0].displayName != ""){
					 	$("#workflow-param-display-name").append("For " + response[0].displayName);
					}
			 	}
		
			 	//if(isPaginationAction){
			 			decreaseCountLoadProcess();
			 	//}
		
			 	if(getCountLoadProcess() == 0){
			// 		console.log("hide in load");
			 		$("#download-spinner").hide();
			 	}
			 	viewResult(response, url);
		 	},
			error:function (xhr, error/*ajaxOptions, thrownError*/){
				showError(xhr, error, $("#tree-error"));
				clearTimeout(timeoutId);	
				decreaseCountLoadProcess();
	        }
		});
	}
	// do inactive pagination link
 	function doInactiveLink(link){
		$(link).addClass("m-inactive").unbind("click");
  	}

  	// do active pagination link in Selected Downlods, Selected Input panels 
  	function doActiveDownloadLink(link, baseUrl){
		$(link).removeClass("m-inactive").unbind("click").bind('click', function(){
			var action = $(this).attr('operation-type');
			var url = "<c:url value='"+ baseUrl +".htm'/>" + "?action=" + action + "&key=" + getRandomInt();
			loadSelectedDownload(url, true, startTimeOut());
		});
	}

	//do active pagination link in TreeView
  	function doActiveTreeLink(link){
  		$(link).removeClass("m-inactive").unbind("click").bind('click', function(){

		var root = $(this).parent();
		var id = $(root).attr('tree-id');
		var action = $(this).attr('operation-type');
		var itemsPage = $(root).parent().find('select[name="page_size"] option:selected').val();
	
		var type_class = $("#root-" + id).find("> ul").attr("class");
		$("#root-" + id).empty().append("<ul id='" + id + "' class='" + type_class + "'></ul>");
	
		var ajaxUrl = $('#root-' + id).attr('ajax-url');
		var typeList = $('#root-' + id).attr('type-list');
			 
		//IS_LOAD_FILES_FINISHED = true;
		$("#" + id).treeview({
			collapsed: true,
		   	url: ajaxUrl +"?action=" + action + "&typeList=" + typeList + "&key=" + getRandomInt() + "&pi=" + itemsPage,
		   	spinner: $("#download-spinner"),
			treeId: id
		  });
		});
	}

	function doActiveStartDonwnloadLink(){
		$("#start-download").removeAttr('onclick').bind('click', function() {
			var a = this;
			incrementCountLoadProcess();
			var url = "bulkDownloaderValidator.htm";
			$("#tree-error").empty();
			$.ajax({
				type: 'POST',
			 	url: url,
			 	dataType: "text",
			 	success:  function(response) {
				 	clearTimeout(timeoutId);
		 			decreaseCountLoadProcess();
		
				 	if(getCountLoadProcess() == 0){
				 		$("#download-spinner").hide();
				 	}

					var result = eval(response);
					
					// show popup if any
					if (result.text.length != 0) {
						alert(result.text);
					} else {
						// Everything is fine. Let's get that files.
						window.location = "bulkDownloader.htm";
					}
			 	},
				error:function (xhr, error/*ajaxOptions, thrownError*/){
					showError(xhr, error, $("#tree-error"));
					clearTimeout(timeoutId);	
					decreaseCountLoadProcess();
				}
			});
		});
	}
	function doInactiveStartDonwnloadLink(){
		$("#start-download").removeAttr('onclick').bind('click', function() { return false });		
	}

	function viewResult(response, url){
		var list;
		var info;

	 	var blockLinks;
	 	var baseUrl;
	 	var first, previous, next, last; 
	 
	 	if(url.indexOf("Input") > 0){
			list = $("#study-input");
			info = $("#page-info-input");

			blockLinks = $("#input-pagination-links");
			baseUrl = "selectInputList";
	 	}else{
		 	list = $("#study-file");
		 	info = $("#page-info-file");

		 	blockLinks = $("#file-pagination-links");
		 	baseUrl = "downloadFileList";

			// do inactive Start download
		 	if(response[0].files.length == 0 &&	 response[0].isStart == "true" && response[0].isEnd == "true"){
		 		doInactiveStartDonwnloadLink();
		 	}else{
				doActiveStartDonwnloadLink();
			}
	 	}

		first = $(blockLinks).children("a[operation-type='first']");
	  	previous = $(blockLinks).children("a[operation-type='previous']");
	  	next = $(blockLinks).children("a[operation-type='next']");
	 	last = $(blockLinks).children("a[operation-type='last']");

	 	if(response[0].files.length == 0 && url.indexOf("Input") == - 1 &&
	 		 	response[0].isStart == "true" && response[0].isEnd == "true")
 		{
	 		$("#start-download").attr('onclick', 'return false;');
	 		$("#cancel-download").attr('onclick', 'return false;');
	 	}else{
	 		$("#start-download").attr('onclick', '');
	 		$("#cancel-download").attr('onclick', '');
	 	}
	
	  	if(response[0].isStart == "true"){
	 		doInactiveLink(first);
	 		doInactiveLink(previous);
	  	}

	  	if(response[0].isStart == "false"){
			doActiveDownloadLink(first,baseUrl);
			doActiveDownloadLink(previous, baseUrl);
	  	}

		if(response[0].isEnd == "true"){
	 		doInactiveLink(next);
	 		doInactiveLink(last);
	  	}

		if(response[0].isEnd == "false"){
			doActiveDownloadLink(next, baseUrl);
			doActiveDownloadLink(last, baseUrl);
	 	}
	 
	 	$(list).empty();
	 	for(i=0; i<response[0].files.length; i++){
			$(list).append("<li><a href='javascript:void(0)'>" + response[0].files[i].text + "</a></li>");
	 	}

	 	$(info).empty().append(response[0].pageInfo);
  	}

  	jQuery.fn.check = function(valueClass, text) {	   
		return this.each(function(){
			$(this).attr('class', valueClass);
			$(this).html(text);
		});
	};
</script>
  
<script type="text/javascript">
	var supernote = new SuperNote('supernote', {});
	hideDelay: 100
	function animFade(ref, counter)
	{
		 
	 var f = ref.filters, done = (counter == 1);
	 if (f)
	 {
	  if (!done && ref.style.filter.indexOf("alpha") == -1)
	   ref.style.filter += ' alpha(opacity=' + (counter * 100) + ')';
	  else if (f.length && f.alpha) with (f.alpha)
	  {
	   if (done) enabled = false;
	   else { opacity = (counter * 100); enabled=true }
	  }
	 }
	 else ref.style.opacity = ref.style.MozOpacity = counter*0.999;
	};
	supernote.animations[supernote.animations.length] = animFade;
</script>

<script language="javascript" >
	$(function() {
		$( "#tabs" ).tabs();
	});
</script>
	 
</head>
<body class="m-inner m-private">
	<div class="h-base">
	    <tiles:insert attribute="header"/>
	    <div class="l-body">
	        <div class="h-content m-inner">
	     	    <div class="b-col-1">
					<tiles:insert attribute="navigation"/>
	            </div>
	            <div class="b-col-2">
	            	<tiles:insert attribute="content"/>
	            </div>
	        </div>
	    </div>
	</div>
	<tiles:insert attribute="footer"/>
</body>

</html>
