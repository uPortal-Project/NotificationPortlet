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

    var getJobs = function() {
        var jqxhr = $.getJSON(settings.urls.getNotificationsUrl, function(data) {
            jobList = data.feed;
            // Extract unique categories from entry attributes
            categories = _.uniq(_.compact(_.map(jobList, function(entry) {
                return entry.attributes && entry.attributes.category ? entry.attributes.category[0] || entry.attributes.category : null;
            })));
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
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_hiringCenters').html())({ offices: offices });
        $('#' + settings.portletId + ' .hiringCenters').html(tmpl);
    };
    var displayCategories = function() {
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_categories').html())({ categories: categories });
        $('#' + settings.portletId + ' .filter-categories').html(tmpl);
    };

    var clearFilter = function() {
        oTable.search('').columns().search('').draw();
        populateHiringOffices();
        displayCategories();
    };

    var displayJob = function(row) {
        var data = oTable.row(row).data();
        data.emailFriend = settings.emailFriend;
        populateTemplate(data);
    };

    var toggleFavorite = function(el) {
        var star = $(el).children('i');

        var jqxhr = $.get( el.href, function() {
          star.toggleClass('fa-star-o fa-star');
        })
        .fail(function() {
        });

        var cell = oTable.cell(el.parentNode);
        cell.data(!cell.data());
    };

    var populateTemplate = function(data) {
        var tmpl = _.template($('#' + settings.portletId + 'tmpl_jobDescriptionModal').html())(data);
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
        var modalEl = document.querySelector('#' + settings.portletId + ' .jobDetailsModal');
        bootstrap.Modal.getOrCreateInstance(modalEl).toggle();
    };

    var applyFilter = function(def, col) {
        if (col !== null && col !== undefined) {
            oTable.column(col).search(def, true, false).draw();
        } else {
            oTable.search(def).draw();
        }
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
        var portletId = settings.portletId;

        // Custom date filter
        $.fn.dataTable.ext.search.push(
            function(dtSettings, data, dataIndex) {
                var min = document.getElementById(portletId + 'date-range').value;
                if (min === '' ) {
                    return true;
                }
                min = Date.parse(min);
                var colDate = Date.parse(data[3]);
                return colDate >= min;
            }
        );

        // Default sort: Date (col 3), newest first
        oTable = new DataTable('#' + settings.portletId + 'jobPostings', {
            data: jobList,
            order: [[ 3, "desc" ]],
            layout: {
                topStart: null,
                topEnd: null,
                bottomStart: ['pageLength', 'info'],
                bottomEnd: 'paging'
            },
            language: {
                lengthMenu: "Show _MENU_ entries"
            },
            columnDefs: [
                {
                    orderable: false,
                    targets: [ 0, 1 ]
                },
                {
                    targets: [ 0 ],
                    width: "10%",
                    data: "attributes.status",
                    render: function ( data, type, full ) {
                        if (data[0].toLowerCase() === 'open') {
                            return '<a href="' + full.url + '" target="_blank" class="btn btn-sm btn-success">Apply</a>';
                        } else {
                            return 'Apply in Person';
                        }
                    },
                    createdCell: function (nTd, sData, oData, iRow, iCol) {
                        if (oData.attributes.status !== 'open') {
                            $(nTd).addClass('small-text');
                        }
                    }
                },
                {
                    targets: [ 1 ],
                    width: "5%",
                    data: "favorite",
                    className: "favorite d-none d-sm-table-cell",
                    render: function ( data, type, full ) {
                        if (full.attributes.status[0].toLowerCase() === 'open') {
                            var url = settings.urls.invokeActionUrlTemplate.replace(/NOTIFICATIONID/,full.id).replace(/ACTIONID/, 'FavoriteAction');
                            if (data === true) {
                                return '<span class="visually-hidden">' + data + '</span><a href="' + url + '" title="Remove from saved jobs"><i class="fa fa-star"></i></a>';
                            } else {
                                return '<span class="visually-hidden">' + data + '</span><a href="' + url + '" title="Add to saved jobs"><i class="fa fa-star-o"></i></a>';
                            }
                        } else {
                            return '';
                        }
                    }
                },
                {
                    targets: [ 2 ],
                    width: "30%",
                    data: "title",
                    className: 'jobTitle',
                    render: function( data, type, full ) {
                        return '<a href="' + full.id + '" title="' + full.linkText + '" class="jobDetailsLink" >' + data + '</a>';
                    }
                },
                {
                    targets: [ 3 ],
                    width: "10%",
                    className: "d-none d-sm-table-cell",
                    data: "attributes.postDate",
                    render: function( data, type, full ) {
                        return data[0];
                    }
                },
                {
                    targets: [ 4 ],
                    width: "15%",
                    data: "id",
                    className: "d-none d-md-table-cell",
                    render: function (data, type, full) {
                        return data;
                    }
                },
                {
                    targets: [ 5 ],
                    data: "attributes.department",
                    className: "d-none d-sm-table-cell"
                },
                {
                    targets: [ 6 ],
                    data: "attributes.category",
                    visible: false
                },
                {
                    targets: [ 7 ],
                    data: "source",
                    visible: false
                },
                {
                    targets: [ 8 ],
                    data: "attributes.description",
                    visible: false
                },
                {
                    targets: [ 9 ],
                    data: "attributes.qualifications",
                    visible: false
                }
            ],
            initComplete: function(dtSettings, json) {

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
                    oTable.draw();
                });

                // Hiring center dropdown filter
                $('#' + settings.portletId + 'hiringCenters').change( function() {
                    applyFilter(this.value, 7);
                });

                $('#' + settings.portletId + 'jobPostings').on( 'click', 'td.favorite a', function(e) {
                    e.preventDefault();
                    toggleFavorite(this);
                });
                $('#' + settings.portletId + 'jobPostings').on( 'click', 'td.jobTitle a', function(e) {
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
                $('.toggleCheckbox').append(cbl.html(tcb).append('Show "apply in person" jobs'));

                removeLoadingOverlay();
            }
        });
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
                $.ajax({
                    type: 'POST',
                    url: settings.urls.invokeNotificationServiceUrl,
                    complete: function() { getJobs(); }
                });
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
