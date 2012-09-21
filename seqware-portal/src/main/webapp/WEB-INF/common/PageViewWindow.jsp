<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="page-view-popup" class="hidden">
	<div class="bug">
		<iframe src="" WIDTH=400 HEIGHT=500 name="iframe" id="iframe" SCROLLING="auto" NORESIZE></iframe>
   		<div class="b-sbmt-field">
   			<a href="javascript:void(0)" id="cancel-index-page">Cancel<br/></a>
   		</div>
	</div>
</div>

<div id="popups">
</div>

<div id="win" style="visibility:hidden">
	<div class="hd"><div class="tl"></div>
		<span>Report</span>
		<div class="tr"></div>
	</div>
	 <!-- POPUP NAVIGATION -->
	<ul class="b-popup-browse">
		<li><a href="javascript:void(0)" class="m-prev m-disable" type="index-oper" action="previous">prev</a></li>
		<li><a href="javascript:void(0)" class="m-next m-disable" type="index-oper" action="next">next</a></li>
		<li><a href="javascript:void(0)" class="m-reload m-disable" type="index-oper" action="reload">reload</a></li>
		<li><a href="javascript:void(0)" class="m-stop m-disable">stop</a></li>
		<li><a href="javascript:void(0)" class="m-home m-disable" type="index-oper" action="home">home</a></li>
	</ul>
                                         
	<div class="bd">
	</div>
</div>