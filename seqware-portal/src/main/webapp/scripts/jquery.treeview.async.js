/*
 * Async Treeview 0.1 - Lazy-loading extension for Treeview
 * 
 * http://bassistance.de/jquery-plugins/jquery-plugin-treeview/
 *
 * Copyright (c) 2007 Jörn Zaefferer
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: $Id$
 *
 */
;(function($) {
	
function load(settings, root, child, container) {
//	$.getJSON(settings.url, {root: root}, function(response) {
	if(settings.spinner){
		incrementCountLoadProcess();
//		console.log("show spinner");
		timeoutId = startTimeOut();//setTimeout(function() {settings.spinner.show(); }, 1000);
		//settings.spinner.show(0, function(){ setTimeout(function() { settings.spinner.hide() }, 1000) })
	//	console.log(timeoutId);	
		settings.timeoutId = timeoutId;
	//	console.log(settings.spinner);
	}
	$("#tree-error").empty();
	$.ajax({url: settings.url, data: {root: root}, dataType: "html", 
		success: function(response) {
			clearTimeout(settings.timeoutId);
			decreaseCountLoadProcess();
		
			var res = eval(response).html[0];
	
			if(res.isStart){
				var blockLinks = $("div[tree-id='"+ settings.treeId +"']");
				blockLinks.children("div:first").empty().append(res.pageInfo);
				
				var first = $(blockLinks).children("a[operation-type='first']");
				var previous = $(blockLinks).children("a[operation-type='previous']");
				var next = $(blockLinks).children("a[operation-type='next']");
				var last = $(blockLinks).children("a[operation-type='last']");
		
				if(res.isStart == "true"){
				 	doInactiveLink(first);
				 	doInactiveLink(previous);
				}
				if(res.isStart == "false"){
				 	doActiveTreeLink(first);
				 	doActiveTreeLink(previous);
				}
				if(res.isEnd == "true"){
				 	doInactiveLink(next);
				 	doInactiveLink(last);
				}
				if(res.isEnd == "false"){
				 	doActiveTreeLink(next);
				 	doActiveTreeLink(last);
				}
				
				// show sort link if text is not empty
				if(res.text != ""){
					$(blockLinks).children("a:first").show();
				}
			}
			if(res.isHasError == true){
				$("#tree-error").append(res.errorMessage);
			}
	
			$(child).append(res.text);
			
		/*	function createNode(parent) {
				var current = $(this.text).appendTo(parent);
				if (this.children && this.children.length) {
					var branch = current.children("ul");
					if (this.children && this.children.length) {
						$.each(this.children, createNode, [branch])
					}
				}
				
			}
			$.each(response, createNode, [child]);
		*/	
		    $(container).treeview({add: child});
	
		    // invoke callback function
		    if (settings.ajaxload) settings.ajaxload();
		    
		    if(getCountLoadProcess()==0) settings.spinner.hide();
			console.log("Load processes left " + getCountLoadProcess());
	    },
/*  	complete: function(response) {
			clearTimeout(settings.timeoutId);
			decreaseCountLoadProcess();
			
			IS_LOAD_TREE_FINISHED = true;
		 	if(getCountLoadProcess()==0){
				settings.spinner.hide();
			}
		},
*/		
		error:function (xhr, error/*ajaxOptions, thrownError*/){
		//	console.log("handle error in tree");
			showError(xhr, error, $("#tree-error"));
	    }   
    });
}

var proxied = $.fn.treeview;
$.fn.treeview = function(settings) {
	if (!settings.url) {
		return proxied.apply(this, arguments);
	}

	// set callback function
	if ( settings.ajaxload ) {
			var callback = settings.ajaxload;
		    settings.ajaxload = function() {
			return callback.apply($(this).parent()[0], arguments);
		};
	}
	var container = this;
	
	load(settings, "source", this, container);
	var userToggle = settings.toggle;
	return proxied.call(this, $.extend({}, settings, {
		collapsed: true,
		toggle: function() {
			var $this = $(this);
			if ($this.hasClass("hasChildren")) {
				var childList = $this.removeClass("hasChildren").find("ul");
				childList.empty();
				load(settings, this.id, childList, container);
			}
			if (userToggle) {
				userToggle.apply(this, arguments);
			}
		}
	}));
};

})(jQuery);
