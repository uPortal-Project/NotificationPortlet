//
//  Lightweight accordion jQuery plugin
//
//  Author: Jacob Lichner
//  Email: jacob.d.lichner@gmail.com
//
(function ($) {
  $.fn.accordion = function (options) {
    
    var defaults = {
      trigger : '.notification-trigger',
      content : '.notification-content',
      symbol  : '.trigger-symbol',
      speed   : 'medium'
    };

    // Merge options object with defaults
    var opts = $.extend(defaults, options);
    
    // Cache DOM elements
    var allTriggers = this.children(opts.trigger),
        allContent  = this.children(opts.content);
    
    // Begin method chain on the triggers
    allTriggers.
    
      // Remove trigger symbol if there's no content
      each(function () {
        var trigger = $(this);
        
        if ( noContent(trigger) ) {
          trigger.find(opts.symbol).css("background", "none");
        }
      })
      
      // Accordion click event
      .click(function () {
        var trigger  = $(this),
            content  = trigger.next(opts.content),
            isHidden = content.is(":hidden");
              
        allTriggers.removeClass("active");
        allContent.stop(true,true).slideUp(opts.speed);
      
        if ( isHidden ) {
          content.stop(true,true).slideDown(opts.speed);
          trigger.addClass("active");
        }
        
        return false;
      })
      
      // Add class 'hover' (because :hover is not widely supported
      // on non-anchor elements)
      .hover(
        function () {
          var trigger = $(this);
          if ( !noContent(trigger) ) {
            trigger.addClass("hover");
          }
        },
        function () {
          $(this).removeClass("hover");
        }
      );
      
      function noContent(trigger) {
        return trigger.next(opts.content).length < 1;
      }
    
    return this;
  }
})(jQuery);