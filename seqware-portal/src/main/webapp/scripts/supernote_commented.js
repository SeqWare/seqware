/*

SUPERNOTE v1.0beta (c) 2005-2006 Angus Turnbull, http://www.twinhelix.com
Altering this notice or redistributing this file is prohibited.

*/


// Common event handler API code.

if (typeof addEvent != 'function')
{
 var addEvent = function(o, t, f, l)
 {
  var d = 'addEventListener', n = 'on' + t, rO = o, rT = t, rF = f, rL = l;
  if (o[d] && !l) return o[d](t, f, false);
  if (!o._evts) o._evts = {};
  if (!o._evts[t])
  {
   o._evts[t] = o[n] ? { b: o[n] } : {};
   o[n] = new Function('e',
    'var r = true, o = this, a = o._evts["' + t + '"], i; for (i in a) {' +
     'o._f = a[i]; r = o._f(e||window.event) != false && r; o._f = null;' +
     '} return r');
   if (t != 'unload') addEvent(window, 'unload', function() {
    removeEvent(rO, rT, rF, rL);
   });
  }
  if (!f._i) f._i = addEvent._i++;
  o._evts[t][f._i] = f;
 };
 addEvent._i = 1;
 var removeEvent = function(o, t, f, l)
 {
  var d = 'removeEventListener';
  if (o[d] && !l) return o[d](t, f, false);
  if (o._evts && o._evts[t] && f._i) delete o._evts[t][f._i];
 };
}

function cancelEvent(e, c)
{
 e.returnValue = false;
 if (e.preventDefault) e.preventDefault();
 if (c)
 {
  e.cancelBubble = true;
  if (e.stopPropagation) e.stopPropagation();
 }
};



// *** MAIN OBJECT CLASS ***

function SuperNote(myName, config)
{
 var defaults = {
  myName: myName,        // Names used for notes of this class.
  allowNesting: false,   // Whether to allow triggers within triggers.
  cssProp: 'visibility', // CSS property used to show/hide notes and values.
  cssVis: 'inherit',
  cssHid: 'hidden',
  IESelectBoxFix: true,  // Enables the IFRAME select-box-covering fix.
  showDelay: 0,          // Millisecond delays.
  hideDelay: 500,
  animInSpeed: 0.1,           // Animation speed, from 0.0 to 1.0; 1.0 disables.
  animOutSpeed: 0.1,
  animations: [],        // Array of animation plugins.
  mouseX: 0, mouseY: 0,  // Live mouse co-ords.
  notes: {},             // Store for note timers/references.
  rootElm: null,         // The outermost element, handles mouse events.
  onshow: null,          // Events you can capture. Passed the note ID.
  onhide: null
 };

 for (var p in defaults)
  this[p] = (typeof config[p] == 'undefined') ? defaults[p] : config[p];

 // Capture mouse events on the entire document.
 var obj = this;
 addEvent(document, 'mouseover', function(evt) { obj.mouseHandler(evt, 1) } );
 addEvent(document, 'click', function(evt) { obj.mouseHandler(evt, 2) } );
 addEvent(document, 'mousemove', function(evt) { obj.mouseTrack(evt) } );
 addEvent(document, 'mouseout', function(evt) { obj.mouseHandler(evt, 0) } );

 // Record this instance in our 'instances' array (for setTimeouts).
 this.instance = SuperNote.instances.length;
 SuperNote.instances[this.instance] = this;
}
// Collection of all objects created.
SuperNote.instances = [];
// A list of note behaviour and positioning type handlers.
SuperNote.prototype.bTypes = {};
SuperNote.prototype.pTypes = {};





// *** DEFAULT TYPE HANDLERS ***

// Handlers are called as the note's state changes. They are passed:
// 1) An object reference to this SuperNote object.
// 2) The ID of the note in question.
// 3) The proposed next 'visible' flag of the note.
// 4) The proposed next 'animating' flag of the note ("positional" only).
//
// There are two types: "pTypes" are positional handlers, "bTypes" behavioural.
// Use positional handlers to perform actions when notes show/hide.
// Use behavioural handlers to cancel default actions; they're called when
// notes are about to animate, and can "return false" to cancel it.
//
// Your might find it handy to set notes[noteID].ref.style.left and .top to
// position the note. Also, checkWinX/Y() are functions accepting a note
// reference and proposed position that modify the position so the note will
// display within the borders of the current browser window.

SuperNote.prototype.pTypes.mouseoffset = function(obj, noteID, nextVis, nextAnim) { with (obj)
{
 // Position on first show, and respect both shows and hides.
 var note = notes[noteID];
 if (nextVis && !note.animating && !note.visible)
 {
  note.ref.style.left = checkWinX(mouseX, note) + 'px';
  note.ref.style.top = checkWinY(mouseY, note) + 'px';
 }
}};


SuperNote.prototype.pTypes.mousetrack = function(obj, noteID, nextVis, nextAnim) { with (obj)
{
 // Position every few milliseconds the entire time it's visible.
 var note = notes[noteID];
 if (nextVis && !note.animating && !note.visible)
 {
  var posString = 'with (' + myName + ') {' +
   'var note = notes["' + noteID + '"];' +
   'note.ref.style.left = checkWinX(mouseX, note) + "px";' +
   'note.ref.style.top = checkWinY(mouseY, note) + "px" }';
  eval(posString);
  obj.IEFrameFix(noteID, 1);
  if (!note.trackTimer) note.trackTimer = setInterval(posString, 50);
 }
 else if (!nextVis && !nextAnim)
 {
  clearInterval(note.trackTimer);
  note.trackTimer = null;
 }
}};


SuperNote.prototype.pTypes.triggeroffset = function(obj, noteID, nextVis, nextAnim) { with (obj)
{
 // Find the trigger position and offset the note from that.
 var note = notes[noteID];
 if (nextVis && !note.animating && !note.visible)
 {
  var x = 0, y = 0, elm = note.trigRef;
  while (elm)
  {
   x += elm.offsetLeft;
   y += elm.offsetTop;
   elm = elm.offsetParent;
  }
  note.ref.style.left = checkWinX(x, note) + 'px';
  note.ref.style.top = checkWinY(y, note) + 'px';
 }
}};


SuperNote.prototype.bTypes.pinned = function(obj, noteID, nextVis) { with (obj)
{
 // Ignore hide requests.
 return (!nextVis) ? false : true;
}};





// *** OBJECT METHODS ***

SuperNote.prototype.docBody = function()
{
 return document[(document.compatMode &&
  document.compatMode.indexOf('CSS') > -1) ? 'documentElement' : 'body'];
};
SuperNote.prototype.getWinW = function()
{
 return this.docBody().clientWidth || window.innerWidth || 0;
};
SuperNote.prototype.getWinH = function()
{
 return this.docBody().clientHeight || window.innerHeight || 0;
};
SuperNote.prototype.getScrX = function()
{
 return this.docBody().scrollLeft || window.scrollX || 0;
};
SuperNote.prototype.getScrY = function()
{
 return this.docBody().scrollTop || window.scrollY || 0;
};

SuperNote.prototype.checkWinX = function(newX, note) { with (this)
{
 // Takes a note and a proposed X position, and modifies the position so that
 // the note will fit within the border of the visible browser window.

 return Math.max(getScrX(), Math.min(newX, getScrX() + getWinW() - note.ref.offsetWidth - 8));
}};
SuperNote.prototype.checkWinY = function(newY, note) { with (this)
{
 return Math.max(getScrY(), Math.min(newY, getScrY() + getWinH() - note.ref.offsetHeight - 8));
}};


SuperNote.prototype.mouseTrack = function(evt) { with (this)
{
 // Stores the mouse coordinates as it moves.

 mouseX = evt.pageX || evt.clientX + getScrX() || 0;
 mouseY = evt.pageY || evt.clientY + getScrY() || 0;
}};


SuperNote.prototype.mouseHandler = function(evt, show) { with (this)
{
 // This is called from an onmouseover/click/mouseout handler in the document.
 // show: 0 = mouseout, 1 = mouseover, 2 = click.
 // It loops up the DOM from the event source, initialises element references,
 // and forwards the event to display() for processing.

 if (!document.documentElement) return true;

 var srcElm = evt.target || evt.srcElement,
  // Match CLASS="objectname-hover-foobar" or "objectname-click-foobar".
  trigRE = new RegExp(myName + '-(hover|click)-([a-z0-9]+)', 'i'),
  // Match ID="objectname-target-foobar"
  targRE = new RegExp(myName + '-(note)-([a-z0-9]+)', 'i'),
  // Whether to repsect a found trigger or not (for nesting triggers).
  trigFind = 1,
  // A list of found notes in the current DOM tree.
  foundNotes = {};

 if (srcElm.nodeType != 1) srcElm = srcElm.parentNode;
 var elm = srcElm;
 while (elm && elm != rootElm)
 {
  // Match our regexes, and keep going upwards to detect nested notes!
  if (targRE.test(elm.id) || (trigFind && trigRE.test(elm.className)))
  {
   if (!allowNesting) trigFind = 0;
   var click = RegExp.$1 == 'click' ? 1 : 0,
    noteID = RegExp.$2,
    ref = document.getElementById(myName + '-note-' + noteID),
    trigRef = trigRE.test(elm.className) ? elm : null;

   // If the target exists...
   if (ref)
   {
    // Init data store, keyed on the last part of the tag class/id.
    if (!notes[noteID])
    {
     notes[noteID] = {
      click: click,
      ref: ref,
      trigRef : null,
      visible: 0,
      animating: 0,
      timer: null
     };
     // Also store references in the note DOM object.
     ref._sn_obj = this;
     ref._sn_id = noteID;
    }
    var note = notes[noteID];

    // Add to our list of found notes on this loop up the DOM.
    // Don't "find" clicks on triggers; they're 'outside' clicks, hiding note.
    if (!note.click || (trigRef != srcElm)) foundNotes[noteID] = true;

    // Show/hide hover notes, and show onclick notes.
    if (!note.click || (show == 2))
    {
     // If this is a trigger, record this as the note's current trigger.
     // Also record the trigger reference on the note DOM object for looping.
     if (trigRef)
      notes[noteID].trigRef = notes[noteID].ref._sn_trig = elm;
     // Now call display() to show/record it, if its visibility has changed.
     display(noteID, show);
     // Click notes: avoid navigating the entire document :).
     if (note.click && (srcElm == trigRef)) cancelEvent(evt);
    }
   }
  }

  // Otherwise check the next element up the DOM.
  if (elm._sn_trig)
  {
   // If this is a note target, recurse to its original trigger and allow
   // further triggers to be found and activated.
   trigFind = 1;
   elm = elm._sn_trig;
  }
  else
  {
   elm = elm.parentNode;
  }
 }

 // Now we've finished looping up the DOM, do a final loop through our list
 // of notes, checking to see if any onclick notes need hiding.
 if (show == 2) for (var n in notes)
 {
  if (notes[n].click && notes[n].visible && !foundNotes[n]) display(n, 0);
 }
}};


SuperNote.prototype.display = function(noteID, show) { with (this)
{
 // Called to manage show/hide timers for a specified target.

 with (notes[noteID])
 {
  // Clear any existing timeout.
  clearTimeout(timer);

  // Check that this note isn't already performing the desired action.
  if (!animating || (show ? !visible : visible))
  {
   // If we're currently animating, use a short timeout.
   // Otherwise revert to the standard show/hide timeout.
   var tmt = animating ? 1 : (show ? showDelay||1 : hideDelay||1);
   timer = setTimeout('SuperNote.instances[' + instance + '].setVis("' +
    noteID + '", ' + show + ', false)', tmt);
  }
 }
}};


SuperNote.prototype.checkType = function(noteID, nextVis, nextAnim) { with (this)
{
 // Processes the two 'types' for each note and returns the results.
 // The behavioural type is called when about to animate; it can "return false"
 // to cancel the pending action.
 // The positioning type is always called whenever an action succeeds.

 var note = notes[noteID], bType, pType;
 if ((/snp-([a-z]+)/).test(note.ref.className)) pType = RegExp.$1;
 if ((/snb-([a-z]+)/).test(note.ref.className)) bType = RegExp.$1;

 if (nextAnim && bType && bTypes[bType] &&
  (bTypes[bType](this, noteID, nextVis) == false)) return false;
 if (pType && pTypes[pType]) pTypes[pType](this, noteID, nextVis, nextAnim);
 return true;
}};


SuperNote.prototype.setVis = function(noteID, show, now) { with (this)
{
 // Called from display() above; sets the note visibility,
 // calls the type handler function, and calls animate() below.
 // Pass 'now' in order to show/hide immediately (skips animation).

 var note = notes[noteID];
 if (note && checkType(noteID, show, 1) || now)
 {
  note.visible = show;
  note.animating = 1;
  animate(noteID, show, now);
 }
}};


SuperNote.prototype.animate = function(noteID, show, now) { with (this)
{
 // Repeatedly called to animate a note in and out (if applicable).

 var note = notes[noteID];

 if (!note.animTimer) note.animTimer = 0;
 if (!note.animC) note.animC = 0;

 with (note)
 {
  clearTimeout(animTimer);
  var speed = (animations.length && !now) ? (show ? animInSpeed : animOutSpeed) : 1;

  if (show && !animC)
  {
   if (onshow) this.onshow(noteID);
   IEFrameFix(noteID, 1);
   ref.style[cssProp] = cssVis;
  }

  animC = Math.max(0, Math.min(1, animC + speed * (show ? 1 : -1)));

  if (document.getElementById && speed < 1)
   for (var a = 0; a < animations.length; a++) animations[a](ref, animC);

  if (!show && !animC)
  {
   if (onhide) this.onhide(noteID);
   IEFrameFix(noteID, 0);
   ref.style[cssProp] = cssHid;
  }

  if (animC != parseInt(animC))
  {
   animTimer = setTimeout(myName + '.animate("' +
    noteID + '", ' + show + ')', 50);
  }
  else
  {
   checkType(noteID, animC ? 1 : 0, 0);
   note.animating = 0;
  }
 }
}};


SuperNote.prototype.IEFrameFix = function(noteID, show) { with (this)
{
 // Positions a hidden IFRAME under a note to allow it to cross over <select>
 // boxes in MSIE.

  if (!window.createPopup || !IESelectBoxFix) return;

  var note = notes[noteID], ifr = note.iframe;
  if (!ifr)
  {
   ifr = notes[noteID].iframe = document.createElement('iframe');
   ifr.style.filter = 'progid:DXImageTransform.Microsoft.Alpha(opacity=0)';
   ifr.style.position = 'absolute';
   ifr.style.borderWidth = '0';
   note.ref.parentNode.insertBefore(ifr, note.ref.parentNode.firstChild);
  }

  if (show)
  {
   ifr.style.left = note.ref.offsetLeft + 'px';
   ifr.style.top = note.ref.offsetTop + 'px';
   ifr.style.width = note.ref.offsetWidth + 'px';
   ifr.style.height = note.ref.offsetHeight + 'px';
   ifr.style.visibility = 'inherit';
  }
  else
  {
   ifr.style.visibility = 'hidden';
  }

}};