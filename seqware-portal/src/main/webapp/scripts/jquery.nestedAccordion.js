/* ---------------------------------------------
Nested Accordion v.1.4.3
Script to create 'accordion' functionality on a hierarchically structured content.
http://www.adipalaz.com/experiments/jquery/nested_accordion_hover_demo.html
Requires: jQuery v1.3+
Copyright (c) 2009 Adriana Palazova
Dual licensed under the MIT (http://www.adipalaz.com/docs/mit-license.txt) and GPL (http://www.adipalaz.com/docs/gpl-license.txt) licenses.
------------------------------------------------ */
(function($) {
//$.fn.orphans - http://www.mail-archive.com/jquery-en@googlegroups.com/msg43851.html
$.fn.orphans = function(){
var txt = [];
this.each(function(){$.each(this.childNodes, function() {
  if (this.nodeType == 3 && $.trim(this.nodeValue)) txt.push(this)})}); return $(txt);};
  
$.fn.accordion = function(options) {
    var defaults = {
        obj : 'ul', //the element that contains the accordion - 'ul', 'ol', 'div' 
        objClass : '.accordion', //the class name of the accordion
        objID : '', //the ID of the accordion (optional)
        wrapper :'li', //the common parent of 'a.trigger' and 'o.next' - 'li', 'div'
        el : 'li', //the parent of 'a.trigger' - 'li', '.h'
        head : '', //the headings that are parents of 'a.trigger' (if any)
        next : 'ul', //the collapsible element - 'ul', 'ol', 'div'
        initShow : '', //the initially expanded section (optional)
        showMethod : 'slideDown', //'slideDown', 'show', 'fadeIn', or custom
        hideMethod : 'slideUp', //'slideUp', 'hide', 'fadeOut', or custom
        showSpeed: 400,
        hideSpeed: 800,
        activeLink : true, //'true' if the accordion is used for site navigation
        event : 'click', //'click', 'hover'*
        collapsible : true, //'true' - makes the accordion fully collapsible, 'false' - forces one section to be open at any time
        standardExpansible : true // if 'true', the functonality will be standard Expand/Collapse without 'accordion' effect
    };
    // * The option {event:'hover'} requires an additional plug-in that adds a small delay and prevents the accidental activation of animations 
    // (e.g., http://blog.threedubmedia.com/2008/08/eventspecialhover.html)
    
    var o = $.extend({}, defaults, options);
    
   
    return this.each(function() {
        var containerID = '#' + this.id,
          Obj = containerID + ' ' + o.obj + o.objID + o.objClass,
          El = Obj + ' ' + o.el,
          loc = window.location.href;

        $(Obj).find(o.head).addClass('h');
        
        $(El).each(function(){
          var $node = $(this);
          if ($node.find(o.next).length || $node.next(o.next).length) {
            if ($node.find('> a').length) {
                $node.find('> a').addClass("trigger").css('display', "block").attr('title', "open/close");
            } else {
                $node.orphans().wrap('<a class="trigger" style="display:block" href="#" title="open/close" />');
            }
          } else {$node.addClass('last-child');}
        });
        
        $(El + '+ div:not(.outer)').wrap('<div class="outer" />'); 

        $(Obj + ' .h').each(function(){
          var $this = $(this);
          if (o.wrapper == 'div') {$this.add( $this.next('div.outer') ).wrapAll('<div class="new"></div>');}
        }); 
        
        $(El + ' a.trigger').closest(o.wrapper).find('> ' + o.next).hide();
        
        
        if (o.activeLink) {$(Obj + ' a:not([href $= "#"])[href="' + loc + '"]').addClass('active').closest(o.next).addClass('current');}

        $(Obj).find(o.initShow).show()
          .parents(o.next).show().end()
          .parents(o.wrapper).find('> a.trigger, > ' + o.el + ' a.trigger').addClass('open');
          
        if (o.event == 'click') {
            var ev = 'click';
        } else  {
            var ev = [o.event] + ' focus';
        }
        $(El).find('a.trigger').bind(ev, function() {
            var $thislink = $(this),
                $nextEl = $(this).closest(o.wrapper).find('> ' + o.next),
                $siblings = $(this).closest(o.wrapper).siblings(o.wrapper);
                if (($nextEl).length && ($nextEl.is(':visible')) && (o.collapsible)) {
                    $(this).removeClass('open');
                    $nextEl.filter(':visible')[o.hideMethod](o.hideSpeed);
                }
                if (($nextEl).length && ($nextEl.is(':hidden'))) {
                    if (!o.standardExpansible) {
                      //$siblings.find('> a.open, >'+ o.el + ' a.open').removeClass('open').end()
                      //.find('> ' + o.next + ':visible')[o.hideMethod](o.hideSpeed);
                    }
                    $(this).addClass('open');
                    $nextEl[o.showMethod](o.showSpeed);
                }
                if (o.event != 'click') {
                    $thislink.click(function() {
                        $thislink.blur();
                        if ($thislink.attr('href')== '#') {
                            $thislink.blur();
                            return false;
                        }
                    });
                }
                if (o.event == 'click') return false;
        });
    });
};
})(jQuery);
///////////////////////////
// The plugin can be invoked, for example, like this:
/* ---
$(function() {
/// Standard nested lists:
  $('#container1').accordion();
  // this will expand the sub-list with "class=current", when the accordion is initialized:
  $('#container1').accordion({initShow : "ul.current"});
  
  // N.B. When using event : 'hover', it is needed an additional plug-in that binds a "hover" handler with a small delay.
  // this will expand/collapse the sub-list when the mouse hovers over the trigger element:
  $('#container1').accordion({event : "hover", initShow : "ul.current"});
  
/// Nested Lists + Headings + DIVs:
  $('#container2').accordion({el: '.h', head: 'h4, h5', next: 'div'});
  $('#container2').accordion({el: '.h', head: 'h4, h5', next: 'div', initShow : 'div.outer:eq(1)'});
  
/// Nested DIVs + Headings:
  $('#container2').accordion({obj: 'div', wrapper: 'div', el: '.h', head: 'h4, h5', next: 'div.outer'});
  $('#container2').accordion({objID: '#acc2', obj: 'div', wrapper: 'div', el: '.h', head: 'h4, h5', next: 'div.outer', initShow : 'div.outer:eq(0)'});
});
--- */
