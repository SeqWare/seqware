var allTags=new Array();
var allTags2=new Array();
var allTags3=new Array();


function getClass(theClass,ids) {


var allTags=document.getElementsByTagName('div');

	for (i=0; i<allTags.length; i++) {

		if (allTags[i].className==theClass) {
		eval('allTags[i].style.display="none"');
		}
	}
var allTags2=document.getElementsByTagName('a');	
   
    for (i=0; i<allTags2.length; i++){
		
	if (allTags2[i].className=='b-hero-page m-active') {
		allTags2[i].className='b-hero-page';
		}
		
	}
   
   document.getElementById(ids).style.display='block';
		
}

function showLogin(theId, btn_id) {
 if (document.getElementById(theId).style.display!='block')
   document.getElementById(theId).style.display='block';  
 else if (document.getElementById(theId).style.display!='none')
   document.getElementById(theId).style.display='none';
if(btn_id.className=='m-login')	
   btn_id.className='m-login m-active';
else
if(btn_id.className=='m-login m-active')	
   btn_id.className='m-login';
}
