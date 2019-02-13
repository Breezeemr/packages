(function () {

  var bigCalendar = require('./build/index.js');
  var bigCalendarDragAndDrop = require('./build/addons/dragAndDrop/index.js');

  window["bigCalendar"] = bigCalendar;
  window["bigCalendarDragAndDrop"] = bigCalendarDragAndDrop;
})();
