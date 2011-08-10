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
      speed   : 'medium'
    };

    var opts = $.extend(defaults, options);
    
    var allTriggers = this.children(opts.trigger);
    var allContent  = this.children(opts.content);
    
    allTriggers
      .click(function () {
        var thisTrigger = $(this);
        var thisContent = thisTrigger.next(opts.content);
        var isHidden    = thisContent.is(":hidden");      
      
        allTriggers.removeClass("active");
        allContent.stop(true,true).slideUp(opts.speed);
      
        if ( isHidden ) {
          thisContent.stop(true,true).slideDown(opts.speed);
          thisTrigger.addClass("active");
        }

        return false;
      })
      .hover(
        function () { $(this).addClass("hover");    },
        function () { $(this).removeClass("hover"); }
      );
    
    return this;
  }
})(jQuery);