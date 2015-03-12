<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib prefix="date" uri="http://org.jasig.portlet/NotificationPortlet/date" %>

<c:set var="n"><portlet:namespace/></c:set>

<portlet:actionURL var="invokeNotificationServiceUrl" escapeXml="false">
    <portlet:param name="uuid" value="${uuid}"/>
    <portlet:param name="action" value="invokeNotificationService"/>
</portlet:actionURL>

<%--
 Favorites URL 
    - replace notificationid with jobId
    - replace actionId with favorite/unfavorite
--%>
<portlet:actionURL var="invokeActionUrlTemplate" escapeXml="false">
    <portlet:param name="notificationId" value="NOTIFICATIONID"/>
    <portlet:param name="actionId" value="ACTIONID"/>
</portlet:actionURL>

<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css" />
<link rel="stylesheet" href="<c:url value="/css/job-postings.css"/>" type="text/css"></link>
<!--[if lt IE 10]>
<style>
    .job-postings .searchControls ul li {
        width: 50%;
        float: left
    }
</style>
<![endif]-->

<script src="//code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/jquery.dataTables/1.9.4/jquery.dataTables.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js"></script>

<script type="text/javascript">
    // Bootstrap javascript fails if included multiple times on a page.
    // uPortal Bootstrap best practice: include bootstrap if and only if it is not present and save it to
    // portlets object. Bootstrap functions could be manually invoked via portlets.bootstrapjQuery variable.
    // All portlets using Bootstrap Javascript must use this approach.  Portlet's jQuery should be included
    // prior to this code block.

    var portlets = portlets || {};
    // If bootstrap is not present at uPortal jQuery nor a community bootstrap, dynamically load it.
    up.jQuery().carousel || portlets.bootstrapjQuery || document.write('<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"><\/script>');

</script>
<script type="text/javascript">
    // Must be in separate script tag to insure bootstrap was dynamically loaded.
    portlets["${n}"] = {};
    portlets["${n}"].jQuery = jQuery.noConflict(true);
    // If bootstrap JS global variable was not defined, set it to the jQuery that has bootstrap attached to.
    portlets.bootstrapjQuery = portlets.bootstrapjQuery || (up.jQuery().carousel ? up.jQuery : portlets["${n}"].jQuery);
</script>
<script src='<c:url value="/scripts/job-postings.js"/>'></script>

<div class="job-postings bootstrap-styles" id="${n}">
    <div id="loading" class="container-fluid">
        <div id="floatingCirclesG">
            <div class="f_circleG" id="frotateG_01"></div>
            <div class="f_circleG" id="frotateG_02"></div>
            <div class="f_circleG" id="frotateG_03"></div>
            <div class="f_circleG" id="frotateG_04"></div>
            <div class="f_circleG" id="frotateG_05"></div>
            <div class="f_circleG" id="frotateG_06"></div>
            <div class="f_circleG" id="frotateG_07"></div>
            <div class="f_circleG" id="frotateG_08"></div>
        </div>
    </div>

    <div id="jobView">
        <nav class="navbar navbar-default" role="navigation">
          <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
              <ul class="nav navbar-nav primary-nav">
                <li class="active"><a href="" data-action="all" id="navSearch">Search</a></li>
                <li><a href="" data-action="saved" id="navSaved">Saved Jobs</a></li>
              </ul>
              <ul class="nav navbar-nav navbar-right secondary-nav">
                <li><a href="${portletPreferencesValues['notifications-external-saved-searches-url'][0]}" target="_blank">Saved Searches</a></li>
                <li><a href="${portletPreferencesValues['notification-career-tools-url'][0]}" target="_blank">Career Tools</a></li>
              </ul>
            </div><!-- /.navbar-collapse -->
          </div><!-- /.container-fluid -->
        </nav>

        <div class="container-fluid" style="margin-top: 10px">
            <div class="row">
                <div class="searchControls search-form col-md-5">
                    <h3>Search</h3>
                    <form role="form">
                      <div class="form-group">
                        <label for="${n}searchTerms" class="sr-only">Search Terms:</label>
                        <input type="text" class="form-control input-sm searchTerms" id="${n}searchTerms" placeholder="Enter keyword, Job Id, etc">
                      </div>
                      <div class="form-group">
                        <label for="${n}date-range" class="sr-only">Date Range:</label>
                        <select class="form-control input-sm date-range" id="${n}date-range">
                            <option value="">Show All Dates</option>
                            <option value="${date:todayMinusDays(7, 'MM/dd/yyyy')}">Past Week</option>
                            <option value="${date:todayMinusMonths(1, 'MM/dd/yyyy')}">Past Month</option>
                            <option value="${date:todayMinusMonths(6, 'MM/dd/yyyy')}">Past 6 Months</option>
                            <option value="${date:todayMinusMonths(12, 'MM/dd/yyyy')}">Past Year</option>
                        </select>
                      </div>
                      <div class="form-group">
                        <label for="${n}hiringCenters" class="sr-only">Hiring Center:</label>
                        <select class="hiring-center form-control input-sm hiringCenters" id="${n}hiringCenters">
                        </select>
                      </div>
                      <%--
                      <button type="button" class="btn btn-primary btn-sm" id="searchButton">Search</button>
                      --%>
                    </form>
                </div>
    <%--            <div class="search-separator col-md-1">- or -</div>--%>
                <div class="search-separator col-md-2"></div>
                <div class="searchControls search-filter col-md-5">
                    <h3>Filter</h3>
                    <form role="form">
                        <div class="row">
                            <div class="col-md-12">
                                <ul class="filter-categories"></ul>
                            </div>
                        </div>
                        <%--
                        <div class="row"><div class="col-md-12"><button type="submit" class="btn btn-primary btn-sm" id="filterButton">Filter</button></div></div>
                        --%>
                    </form>
                </div>
            </div>
            
            <!-- Error output -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="alert alert-danger text-center h5" id="errorOutput" style="display:none;"></div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12">
                    <%-- <label><input type="checkbox" id="toggle-checkbox"> Hide 'apply in person' jobs</label> --%>
                    <div class="table-responsive">
                        <table class="jobs-table table table-striped table-bordered" id="${n}jobPostings">
                            <thead>
                                <tr>
                                    <th></th>
                                    <th></th>
                                    <th>Job Title</th>
                                    <th>Date</th>
                                    <th class="hidden-xs">Job ID</th>
                                    <th>Department</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal -->
        <div class="modal fade job-details jobDetailsModal" id="${n}jobDetailsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-lg">
            <div class="modal-content">
            </div>
          </div>
        </div>
    </div>
</div> <!-- bootstrap-styles -->
<script type="text/template" id="${n}tmpl_hiringCenters">
    <option value="" default>Show All Hiring Centers</div>
    {{
        for (i=0;i<offices.length;i++) {
    }}

        <option value="{{=offices[i]}}">{{=offices[i]}}</option>
    {{
        };
    }}
    
</script>
<script type="text/template" id="${n}tmpl_categories">
    {{ for (i=0;i<categories.length;i++) { }}
        <li class="checkbox"><label><input type="checkbox" value="{{= categories[i] }}">{{= categories[i] }}</label></li>
    {{ } }}

</script>

<script type="text/template" id="${n}tmpl_jobDescriptionModal">
{{ if (emailFriend) { }}
<div id="modal-overlay">
    <div class="container-fluid">
        <form id="emailFriendForm" role="form">
          <div class="form-group">
            <label for="emailAddress"><h4>Enter Email Address:</h4></label>
            <input type="text" class="form-control input-sm" id="emailAddress" name="emailAddress" />
            <input type="hidden" name="jobId" value="{{= id }}">
          </div>
          <button type="button" class="btn btn-default cancelEmailButton" id="${n}cancelEmailButton">Cancel</button>
          <button type="button" class="btn btn-primary sendEmailButton" id="${n}sendEmailButton">Send</button>
        </form>
    </div>
</div>
{{ } }}
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h4 class="modal-title" id="myModalLabel">{{=title}}</h4>
</div>
<div class="modal-body container-fluid">
    <div class="row">
        <div class="col-md-7">
            <div class="row">
                <div class="col-sm-12">
                    <h4>Description</h4>
                </div>
                <div class="col-md-10 col-md-offset-1">
                    {{=attributes.description}}
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <h4>Qualifications</h4>
                </div>
                <div class="col-md-10 col-md-offset-1">
                    {{=attributes.qualifications}}
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <h4>Application Instructions</h4>
                </div>
                <div class="col-md-10 col-md-offset-1">
                    {{=attributes.instructions}}
                </div>
            </div>
        </div>
        <div class="col-md-5">
            <div class="sidebar">
                <div class="row">
                    <div class="col-md-12">
                        <h5>{{=attributes.status}}</h5>
                        <h5>Date Posted: {{=attributes.postDate}}</h5>
                        <h5>Job ID: {{=id}}</h5>
                        <h5>Category: {{=attributes.category}}</h5>
                    </div>
                </div>
                <hr />
                <div class="row">
                    <div class="col-md-12"><h5>Additional Information</h5></div>
                </div>
                <div class="row">
                    <div class="col-md-5">Openings:</div>
                    <div class="col-md-7">{{=attributes.openings}}</div>
                </div>
                <div class="row">
                    <div class="col-md-5">Department:</div>
                    <div class="col-md-7">{{=attributes.department}}</div>
                </div>
                <div class="row">
                    <div class="col-md-5">Location:</div>
                    <div class="col-md-7">{{=attributes.location}}</div>
                </div>
                <div class="row">
                    <div class="col-md-5">Start Date:</div>
                    <div class="col-md-7">{{=attributes.startDate}}</div>
                </div>
                <div class="row">
                    <div class="col-md-5">Shift:</div>
                    <div class="col-md-7">{{=attributes.shift}}</div>
                </div>
                <div class="row">
                    <div class="col-md-5">Hourly Wage:</div>
                    <div class="col-md-7">{{=attributes.hourlyWage}}</div>
                </div>
                <div class="row">
                    <div class="col-md-12 closingDate">Closing Date: {{=attributes.dateClosed}}</div>
                </div>

            </div>
        </div>
    </div>
</div>
<div class="modal-footer">
    <div class="row">
        <div class="col-sm-6 text-left">
        {{ if (emailFriend) { }}
            <button type="button" class="btn btn-default emailFriendButton" id="${n}emailFriendButton">Email to Friend</button>
        {{ } }}
        </div>
        <div class="col-sm-6">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            {{ if (attributes.status[0].toLowerCase() === 'open') { }}
            <a href="{{= url }}" target="_blank" class="btn btn-primary" id="applyButton" data-dismiss="modal">Apply</a>
            {{ } else { }}
            <button type="button" class="btn btn-primary disabled">Apply in Person</button>
            {{ } }}
        </div>
    </div>
            
</div>
</script>

<script type="text/javascript">
    var jp_ = _.noConflict();

    var urls = {
        // Prime the pump with this URL
        invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',

        // Get JSON with this one
        getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>',

        // Favorite/un-favorite
        invokeActionUrlTemplate: '${invokeActionUrlTemplate}',

        testJson: '<c:url value="/scripts/jobs.json"/>'
    }

    jp_.templateSettings = {
      //interpolate : /\{\{(.+?)\}\}/g
      evaluate    : /\{\{([\s\S]+?)\}\}/g,
      interpolate : /\{\{=([\s\S]+?)\}\}/g,
      escape      : /\{\{-([\s\S]+?)\}\}/g
    };

    portlets.bootstrapjQuery(document).ready(function() {
        jobPostings.init(portlets.bootstrapjQuery, jp_, urls, '${n}');
    });
</script>
