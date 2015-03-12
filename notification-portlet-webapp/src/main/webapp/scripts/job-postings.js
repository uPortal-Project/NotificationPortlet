/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var cbArray = [];

function arrayPop(item, ary) {
    var array = ary;
    var i = array.indexOf(item);
    if(i != -1) {
        array.splice(i, 1);
    }
    return array;
}

function today() {
    var lenCheck = function (num) {
        return (num.toString().length > 1) ? num : '0' + num;
    };
    var d = new Date();
    
    return lenCheck(d.getMonth()+1) + "/" + lenCheck(d.getDate()) + "/" + d.getFullYear();
}




/**
 * Job Posting
 */

var jobPostings = function(){

/**
 * New process:
 * 1. Display loading screen (underscore template)
 * 2. done - Do handshake
 * 3. done - Get job list
 *     a. done - If successful proceed..
 *     b. If error: display error message instead of loading screen
 * 4. done - Populate Categories list (underscore template)
 * 5. done - Populate Hiring Center dropdown (underscore template)
 * 6. done - Render datatables
 * 7. done - Hook up all the event handlers (data tables init call?)
 *
 */



    // private variables *******
    var settings = {
        urls: {},
        emailFriend: false,
        portletId: null
    };
    var categories;
    var jsonErrors = [];

    var oTable, _,$;

    var handshake = function() {
        $.getJSON( settings.urls.invokeNotificationServiceUrl);
    };

    var getJobs = function() {
        var jqxhr = $.getJSON(settings.urls.getNotificationsUrl, function(data) {
            categories = data.categories;
            jobList = data.feed;
            jsonErrors = data.errors;
        }).done(function() {
            if(jsonErrors.length > 0) {
                returnError(jsonErrors);
            }
            displayCategories();
            populateHiringOffices();
            initTable();
        })
        .fail(function() {
            alert('Unable to retrieve the list of jobs');
            // Place error template handling call here
            //console.log("error");
        });
    };

    var populateHiringOffices = function() {
        var offices = _.uniq(_.pluck(jobList, 'source'));
        var sortingOffices = [];
        for (var i = 0; i < offices.length; i++) {
            sortingOffices.push(offices[i]);
            sortingOffices.sort();
        }
        offices = sortingOffices;
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_hiringCenters').html(), { offices: offices });
        $('#' + settings.portletId + ' .hiringCenters').html(tmpl);
    };
    var displayCategories = function() {
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_categories').html(), { categories: categories });
        $('#' + settings.portletId + ' .filter-categories').html(tmpl);
    };

    var clearFilter = function() {
        oTable.fnFilterClear();
        populateHiringOffices();
        displayCategories();

    };

    var displayJob = function(row) {
        var data = oTable.fnGetData( row );
        data.emailFriend = settings.emailFriend;

        populateTemplate(data);
    };

    var toggleFavorite = function(el) {
        var star = $(el).children('i');

        var jqxhr = $.get( el.href, function() {
          star.toggleClass('fa-star-o fa-star');
        })
        .fail(function() {
            //console.log("error");
        });

        var aPos = oTable.fnGetPosition( el.parentNode );
        var aData = oTable.fnGetData( aPos[0] );

        oTable.fnUpdate( !aData.favorite, aPos[0], aPos[1], false ); // Single cell
    };

    var populateTemplate = function(data) {
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_jobDescriptionModal').html(), data);
        $('#' + settings.portletId + ' .jobDetailsModal').find('.modal-content').html(tmpl);

        toggleModal();
        /**
         * Modal Button Event handlers
         */
        $('#' + settings.portletId + ' .emailFriendButton').on('click', function(e) {
            $('#' + settings.portletId + ' .modal-overlay').show();
        });
        $('#' + settings.portletId + ' .cancelEmailButton').on('click', function(e) {
            $('#' + settings.portletId + ' .modal-overlay').hide();
        });
        $('#' + settings.portletId + ' .sendEmailButton').on('click', function(e) {
            $.post( "test.php", $('#' + settings.portletId + ' .emailFriendForm').serialize());
            $('#' + settings.portletId + ' .modal-overlay').hide();
            toggleModal();
        });
    };

    var toggleModal = function() {
        $('#' + settings.portletId + ' .jobDetailsModal').modal('toggle');
    };

    var applyFilter = function(def, col) {
        oTable.fnFilter(def, col, true, false);
    }

    var returnError = function(errObj) {
        var errResult = '';

        for (var i = 0; i < errObj.length; i++) {
            var errObjKey = errObj[i];

            errResult += errObjKey.source + ": " + errObjKey.error + "<br/>";
        }
        $('#errorOutput').show().html(errResult);
    }

    var removeLoadingOverlay = function() {
        /**
         * Since the table is rendered, hide the loading overlay
         */
        var node = document.getElementById("loading");
        if (node.parentNode) {
          node.parentNode.removeChild(node);
        }
    };

    var initTable = function() {
        $.fn.dataTableExt.oApi.fnGetHiddenNodes = function (oSettings)
        {
            /* Note the use of a DataTables 'private' function thought the 'oApi' object */
            var anNodes = this.oApi._fnGetTrNodes(oSettings);
            var anDisplay = $('tbody tr', oSettings.nTable);
              
            /* Remove nodes which are being displayed */
            for ( var i=0 ; i<anDisplay.length ; i++ )
            {
                var iIndex = jQuery.inArray( anDisplay[i], anNodes );
                if ( iIndex != -1 )
                {
                    anNodes.splice( iIndex, 1 );
                }
            }
              
            /* Fire back the array to the caller */
            return anNodes;
        };

        /**
         * Date Filter
         */
        $.fn.dataTableExt.afnFiltering.push(
            function(oSettings, aData, iDataIndex) {

                var min = document.getElementById(settings.portletId + 'date-range').value;
                if (min === '' ) {
                    return true;
                }
                min = Date.parse(min);

                var iStartDateCol = 3;
                var iEndDateCol = 3;

                var colDate = Date.parse(aData[iStartDateCol]);

                if ( colDate >= min ) {
                    return true;
                }
                return false;
            }
        );
        // End date Filter

        $.extend( true, $.fn.dataTable.defaults, {
            "sDom": "<'row'<'col-md-6 toggleCheckbox'><'col-md-6 text-right'l>t<'row'<'col-md-6'i><'col-md-6'p>>",
            "sPaginationType": "bootstrap",
            "oLanguage": {
                "sLengthMenu": "Show _MENU_ entries"
            }
        } );
        
        /* Default class modification */
        $.extend( $.fn.dataTableExt.oStdClasses, {
            "sWrapper": "dataTables_wrapper form-inline"
        } );

        $.fn.dataTableExt.oApi.fnFilterClear  = function(oSettings) {
            /* Remove global filter */
            oSettings.oPreviousSearch.sSearch = "";
              
            /* Remove the text of the global filter in the input boxes */
            if (typeof oSettings.aanFeatures.f != 'undefined')
            {
                var n = oSettings.aanFeatures.f;
                for (var i=0, iLen=n.length ; i<iLen ; i++)
                {
                    $('input', n[i]).val('');
                }
            }
              
            /* Remove the search text for the column filters - NOTE - if you have input boxes for these
             * filters, these will need to be reset
             */
            for ( var i=0, iLen=oSettings.aoPreSearchCols.length ; i<iLen ; i++ )
            {
                oSettings.aoPreSearchCols[i].sSearch = "";
            }
              
            /* Redraw */
            oSettings.oApi._fnReDraw(oSettings);
        };

        /* API method to get paging information */
        $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings)
        {
            return {
                "iStart":         oSettings._iDisplayStart,
                "iEnd":           oSettings.fnDisplayEnd(),
                "iLength":        oSettings._iDisplayLength,
                "iTotal":         oSettings.fnRecordsTotal(),
                "iFilteredTotal": oSettings.fnRecordsDisplay(),
                "iPage":          oSettings._iDisplayLength === -1 ?
                    0 : Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
                "iTotalPages":    oSettings._iDisplayLength === -1 ?
                    0 : Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
            };
        };
        /* Bootstrap style pagination control */
        $.extend( $.fn.dataTableExt.oPagination, {
            "bootstrap": {
                "fnInit": function( oSettings, nPaging, fnDraw ) {
                    var oLang = oSettings.oLanguage.oPaginate;
                    var fnClickHandler = function ( e ) {
                        e.preventDefault();
                        if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                            fnDraw(oSettings);
                        }
                    };

                    $(nPaging).append(
                        '<ul class="pagination">'+
                            '<li class="prev disabled"><a href="#"><i class="fa fa-caret-left"></i> '+oLang.sPrevious+'</a></li>'+
                            '<li class="next disabled"><a href="#">'+oLang.sNext+' </a></li>'+
                        '</ul>'
                    );
                    var els = $('a', nPaging);
                    $(els[0]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
                    $(els[1]).bind( 'click.DT', { action: "next" }, fnClickHandler );
                },

                "fnUpdate": function (oSettings, fnDraw) {
                    var iListLength = 5;
                    var oPaging = oSettings.oInstance.fnPagingInfo();
                    var an = oSettings.aanFeatures.p;
                    var i, ien, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

                    if ( oPaging.iTotalPages < iListLength) {
                        iStart = 1;
                        iEnd = oPaging.iTotalPages;
                    }
                    else if ( oPaging.iPage <= iHalf ) {
                        iStart = 1;
                        iEnd = iListLength;
                    } else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
                        iStart = oPaging.iTotalPages - iListLength + 1;
                        iEnd = oPaging.iTotalPages;
                    } else {
                        iStart = oPaging.iPage - iHalf + 1;
                        iEnd = iStart + iListLength - 1;
                    }

                    for ( i=0, ien=an.length ; i<ien ; i++ ) {
                        // Remove the middle elements
                        $('li:gt(0)', an[i]).filter(':not(:last)').remove();

                        // Add the new list items and their event handlers
                        for ( j=iStart ; j<=iEnd ; j++ ) {
                            sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
                            $('<li '+sClass+'><a href="#">'+j+'</a></li>')
                                .insertBefore( $('li:last', an[i])[0] )
                                .bind('click', function (e) {
                                    e.preventDefault();
                                    oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
                                    fnDraw(oSettings);
                                } );
                        }

                        // Add / remove disabled classes from the static elements
                        if ( oPaging.iPage === 0 ) {
                            $('li:first', an[i]).addClass('disabled');
                        } else {
                            $('li:first', an[i]).removeClass('disabled');
                        }

                        if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
                            $('li:last', an[i]).addClass('disabled');
                        } else {
                            $('li:last', an[i]).removeClass('disabled');
                        }
                    }
                }
            }
        } );


        /*
         * TableTools Bootstrap compatibility
         * Required TableTools 2.1+
         */
        if ($.fn.DataTable.TableTools) {
            // Set the classes that TableTools uses to something suitable for Bootstrap
            $.extend( true, $.fn.DataTable.TableTools.classes, {
                "container": "DTTT btn-group",
                "buttons": {
                    "normal": "btn",
                    "disabled": "disabled"
                },
                "collection": {
                    "container": "DTTT_dropdown dropdown-menu",
                    "buttons": {
                        "normal": "",
                        "disabled": "disabled"
                    }
                },
                "print": {
                    "info": "DTTT_print_info modal"
                },
                "select": {
                    "row": "active"
                }
            } );

            // Have the collection use a bootstrap compatible dropdown
            $.extend( true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
                "collection": {
                    "container": "ul",
                    "button": "li",
                    "liner": "a"
                }
            } );
        }

        /**
         * Default sort: Date (col 3), newest first
         */
        $('#' + settings.portletId + 'jobPostings').dataTable( {
            "aaData": jobList,
            "aaSorting": [[ 3, "desc" ]],
            "aoColumnDefs": [
                {
                    "bSortable": false,
                    "aTargets": [ 0, 1 ]
                },
                {
                    "aTargets": [ 0 ],
                    "sWidth": "10%",
                    "mData": "attributes.status",
                    "mRender": function ( data, type, full ) {
                        if (data[0].toLowerCase() === 'open') {
                            return '<a href="' + full.url + '" target="_blank" class="btn btn-sm btn-success">Apply</a>';
                        } else {
                            return 'Apply in Person';//data[0].toLowerCase();
                        }
                    },
                    "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                        if (oData.attributes.status !== 'open') {
                            $(nTd).addClass('small-text');
                        }
                    }

                },
                {
                    "aTargets": [ 1 ],
                    "sWidth": "5%",
                    "mData": "favorite",
                    "sClass": "favorite hidden-xs",
                    "mRender": function ( data, type, full ) {
                        var favClass, linkTitle;
                        if (full.attributes.status[0].toLowerCase() === 'open') {
                            var url = settings.urls.invokeActionUrlTemplate.replace(/NOTIFICATIONID/,full.id).replace(/ACTIONID/, 'FavoriteAction');
                            if (data === true) {
                                return '<span class="sr-only">' + data + '</span><a href="' + url + '" title="Remove from saved jobs"><i class="fa fa-star"></i></a>';
                            } else {
                                return '<span class="sr-only">' + data + '</span><a href="' + url + '" title="Add to saved jobs"><i class="fa fa-star-o"></i></a>';
                            }
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "aTargets": [ 2 ],
                    "sWidth": "30%",
                    "mData": "title",
                    "sClass": 'jobTitle',
                    "mRender": function( data, type, full ) {
                        return '<a href="' + full.id + '" title="' + full.linkText + '" class="jobDetailsLink" >' + data + '</a>';
                    }
                },
                {
                    "aTargets": [ 3 ],
                    "sWidth": "10%",
                    "sClass": "hidden-xs",
                    "mData": "attributes.postDate",
                    "mRender": function( data, type, full ) {
                        //var d = Date.parse(data);
                        return data[0];
                        // return data + '('+d+')';
                    }
                },
                {
                    "aTargets": [ 4 ],
                    "sWidth": "15%",
                    "mData": "id",
                    "sClass": "hidden-xs hidden-sm",
                    "mRender": function (data, type, full) {
                        return data;
                    }
                },
                {
                    "aTargets": [ 5 ],
                    "mData": "attributes.department",
                    "sClass": "hidden-xs"
                },
                {
                    "aTargets": [ 6 ],
                    "mData": "attributes.category",
                    "bVisible": false
                },
                {
                    "aTargets": [ 7 ],
                    "mData": "source",
                    "bVisible": false
                },
                {
                    "aTargets": [ 8 ],
                    "mData": "attributes.description",
                    "bVisible": false
                },
                {
                    "aTargets": [ 9 ],
                    "mData": "attributes.qualifications",
                    "bVisible": false
                }
            ],
            "fnInitComplete": function(oSettings, json) {
                oTable = this;

                // Apply filters
                // Category filters
                $('.searchControls :checkbox').change(function(e) {
                    var cbDef;
                    if ($(this).is(':checked')) {
                        cbArray.push(this.value);
                    } else {
                        arrayPop(this.value, cbArray);
                    }
                    cbDef = cbArray.join('|');
                    applyFilter(cbDef, 6);
                });

                // In person filter
                $('.toggleCheckbox').on('change', '#inPersonCb', function() {
                    var ipDef;
                    if ($('#inPersonCb').is(':checked')) {
                        ipDef = '';
                    } else {
                        ipDef = '^(?:(?!person).)*$\r?\n?';
                    }
                    applyFilter(ipDef, 0);
                });

                // Saved jobs filter
                $('#navSaved').click(function() {
                    applyFilter('true', 1);
                });

                // Remove saved jobs filter
                $('#navSearch').click(function() {
                    applyFilter('', 1);
                });

                // Keyword textbox search filter
                $('#' + settings.portletId + 'searchTerms').keyup(function(e) {
                    applyFilter(this.value, null);
                });

                // Date range dropdown filter
                $('#' + settings.portletId + 'date-range').change( function() {
                    oTable.fnDraw();
                });

                // Hiring center dropdown filter
                $('#' + settings.portletId + 'hiringCenters').change( function() {
                    applyFilter(this.value, 7);
                });

                $('#' + settings.portletId + 'jobPostings').delegate( 'td.favorite a', 'click', function(e) {
                    e.preventDefault();
                    toggleFavorite(this);
                });
                $('#' + settings.portletId + 'jobPostings').delegate( 'td.jobTitle a', 'click', function(e) {
                    e.preventDefault();
                    displayJob($(this).closest('tr')[0]);
                });

                // Toggle active tab colors
                $('.primary-nav').click(function(e) {
                    e.preventDefault();
                    $('.primary-nav').children().removeClass('active');
                    $(e.target).parent().addClass('active');
                });
                
                // Create toggle in person jobs checkbox
                var cbl = $('<label>');
                var tcb = $('<input>', {
                    type:"checkbox",
                    id:"inPersonCb",
                    checked:true
                });
                // cbl.html(tcb);
                $('.toggleCheckbox').append(cbl.html(tcb).append('Show "apply in person" jobs'));

                removeLoadingOverlay();
            }
        } );
    };

    // initialization *******
    ( function init () {
        // console.warn('jobPostings init');
    })();

    // public API *******
    return {
        init: function (myjQuery, myUnderscore, args, portletId) {
            _ = myUnderscore;
            $ = myjQuery;
            settings.portletId = portletId;
            if (args) {
                settings.urls = args;
                handshake();
                getJobs();
            }
        },
        clearFilter: function() {
            clearFilter();
        },
        getTable: function() {
            return oTable;
        }
    };
}();
