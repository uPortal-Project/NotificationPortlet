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

<!-- Bootstrap 5 CSS and JS are provided by the portal skin via resource-server webjars -->
<link rel="stylesheet" href="<c:url value="/css/job-postings.css"/>" type="text/css"></link>

<!-- DataTables 2.x with Bootstrap 5 integration loaded by portal skin via resource-server webjars -->
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
        <nav class="navbar navbar-expand-md" role="navigation">
          <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#bs-example-navbar-collapse-1">
                <span class="visually-hidden">Toggle navigation</span>
                <span class="navbar-toggler-icon"></span>
              </button>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
              <ul class="nav navbar-nav primary-nav">
                <li class="active"><a href="" data-action="all" id="navSearch">Search</a></li>
                <li><a href="" data-action="saved" id="navSaved">Saved Jobs</a></li>
              </ul>
              <ul class="nav navbar-nav ms-auto secondary-nav">
                <li><a href="${portletPreferencesValues['notifications-external-saved-searches-url'][0]}" target="_blank">Saved Searches</a></li>
                <li><a href="${portletPreferencesValues['notification-career-tools-url'][0]}" target="_blank">Career Tools</a></li>
              </ul>
            </div><!-- /.navbar-collapse -->
          </div><!-- /.container-fluid -->
        </nav>

        <div class="container-fluid" style="margin-top: 10px">
            <div class="row justify-content-between">
                <div class="searchControls search-form col-md-5">
                    <h3>Search</h3>
                    <form role="form">
                      <div class="mb-3">
                        <label for="${n}searchTerms" class="visually-hidden">Search Terms:</label>
                        <input type="text" class="form-control form-control-sm searchTerms" id="${n}searchTerms" placeholder="Enter keyword, Job Id, etc">
                      </div>
                      <div class="mb-3">
                        <label for="${n}date-range" class="visually-hidden">Date Range:</label>
                        <select class="form-select form-select-sm date-range" id="${n}date-range">
                            <option value="">Show All Dates</option>
                            <option value="${date:todayMinusDays(7, 'MM/dd/yyyy')}">Past Week</option>
                            <option value="${date:todayMinusMonths(1, 'MM/dd/yyyy')}">Past Month</option>
                            <option value="${date:todayMinusMonths(6, 'MM/dd/yyyy')}">Past 6 Months</option>
                            <option value="${date:todayMinusMonths(12, 'MM/dd/yyyy')}">Past Year</option>
                        </select>
                      </div>
                      <div class="mb-3">
                        <label for="${n}hiringCenters" class="visually-hidden">Hiring Center:</label>
                        <select class="hiring-center form-select form-select-sm hiringCenters" id="${n}hiringCenters">
                        </select>
                      </div>
                      <%--
                      <button type="button" class="btn btn-primary btn-sm" id="searchButton">Search</button>
                      --%>
                    </form>
                </div>
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
                                    <th class="d-none d-sm-table-cell">Job ID</th>
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
          <div class="mb-3">
            <label for="emailAddress"><h4>Enter Email Address:</h4></label>
            <input type="text" class="form-control form-control-sm" id="emailAddress" name="emailAddress" />
            <input type="hidden" name="jobId" value="{{= id }}">
          </div>
          <button type="button" class="btn btn-secondary cancelEmailButton" id="${n}cancelEmailButton">Cancel</button>
          <button type="button" class="btn btn-primary sendEmailButton" id="${n}sendEmailButton">Send</button>
        </form>
    </div>
</div>
{{ } }}
<div class="modal-header">
    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    <h4 class="modal-title" id="myModalLabel">{{=title}}</h4>
</div>
<div class="modal-body container-fluid">
    <div class="row">
        <div class="col-md-7">
            <div class="row">
                <div class="col-sm-12">
                    <h4>Description</h4>
                </div>
                <div class="col-md-10 offset-md-1">
                    {{=attributes.description}}
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <h4>Qualifications</h4>
                </div>
                <div class="col-md-10 offset-md-1">
                    {{=attributes.qualifications}}
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <h4>Application Instructions</h4>
                </div>
                <div class="col-md-10 offset-md-1">
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
            <button type="button" class="btn btn-secondary emailFriendButton" id="${n}emailFriendButton">Email to Friend</button>
        {{ } }}
        </div>
        <div class="col-sm-6">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            {{ if (attributes.status[0].toLowerCase() === 'open') { }}
            <a href="{{= url }}" target="_blank" class="btn btn-primary" id="applyButton" data-bs-dismiss="modal">Apply</a>
            {{ } else { }}
            <button type="button" class="btn btn-primary disabled">Apply in Person</button>
            {{ } }}
        </div>
    </div>
            
</div>
</script>

<script type="text/javascript">
    var jp_ = (typeof up !== 'undefined' && up._) ? up._ : _;

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

    var jp$ = (typeof up !== 'undefined' && up.jQuery) ? up.jQuery : jQuery;
    jp$(document).ready(function() {
        jobPostings.init(jp$, jp_, urls, '${n}');
    });
</script>
