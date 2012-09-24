<!DOCTYPE html> 
<html lang="en"> 
<head> 
  <meta charset="utf-8"> 
  <title>jQuery UI Autocomplete - Multiple, remote</title> 

  <link rel="stylesheet" href="/queryengine/static/css/jquery-ui-1.8.9.custom.css"> 
  <script src="/queryengine/static/js/jquery-1.4.4.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.core.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.widget.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.position.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.autocomplete.js"></script> 
  <link rel="stylesheet" href="/queryengine/static/css/demos.css"> 

  <style> 
  .ui-autocomplete-loading { background: white url('/queryengine/static/images/ui-anim_basic_16x16.gif') right center no-repeat; }
  </style> 
  <script> 
  $(function() {
    function split( val ) {
      return val.split( /,\s*/ );
    }
    function extractLast( term ) {
      return split( term ).pop();
    }
 
    $( "#tags" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {
// FIXME: this is hard-coded!!!!!!
          $.getJSON( "/queryengine/static/variants/tags/6", {
            term: extractLast( request.term )
          }, response );
        },
        search: function() {
          // custom minLength
          var term = extractLast( this.value );
          if ( term.length < 3 ) {
            return false;
          }
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          return false;
        }
      });
  });
  </script> 
</head> 
<body> 
 
<div class="demo"> 
 
<div class="ui-widget"> 
  <label for="tags">Tags Search: </label> 
  <input id="tags" size="150" /> 
</div> 
 
</div><!-- End demo --> 
 
 
 
<div class="demo-description"> 
<p>Usage: Enter at least two characters to get tag suggestions. Select a value to continue adding more names.</p> 
<p>This is an example showing how to use the source-option along with some events to enable autocompleting multiple values into a single field.</p> 
</div><!-- End demo-description --> 
 
</body> 
</html> 