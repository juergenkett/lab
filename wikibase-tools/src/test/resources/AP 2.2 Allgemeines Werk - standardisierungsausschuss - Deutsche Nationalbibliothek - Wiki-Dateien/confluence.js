WRMCB=function(e){var c=console;if(c&&c.log&&c.error){c.log('Error running batched script.');c.error(e);}}
;
try {
/* module-key = 'confluence.extra.masterdetail:master-details-resources', location = 'templates/extra/masterdetail/detailssummary.soy' */
// This file was automatically generated from detailssummary.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace Confluence.Templates.Macro.MasterDetail.
 */

if (typeof Confluence == 'undefined') { var Confluence = {}; }
if (typeof Confluence.Templates == 'undefined') { Confluence.Templates = {}; }
if (typeof Confluence.Templates.Macro == 'undefined') { Confluence.Templates.Macro = {}; }
if (typeof Confluence.Templates.Macro.MasterDetail == 'undefined') { Confluence.Templates.Macro.MasterDetail = {}; }


Confluence.Templates.Macro.MasterDetail.detailsSummary = function(opt_data, opt_ignored) {
  var output = '<div class="table-wrap"><table class="aui metadata-summary-macro ' + soy.$$escapeHtml(opt_data.label) + '" data-cql="' + soy.$$escapeHtml(opt_data.cql) + '"' + ((opt_data.id) ? ' data-details-id="' + soy.$$escapeHtml(opt_data.id) + '" ' : '') + ((opt_data.macroHeadings) ? ' data-headings="' + soy.$$escapeHtml(opt_data.macroHeadings) + '" ' : '') + 'data-first-column-heading="' + soy.$$escapeHtml(opt_data.firstColumnHeading) + '" data-count-comments="' + soy.$$escapeHtml(opt_data.showCommentsCount) + '" data-count-likes="' + soy.$$escapeHtml(opt_data.showLikesCount) + '" data-total-pages="' + soy.$$escapeHtml(opt_data.totalPages) + '" data-page-size="' + soy.$$escapeHtml(opt_data.pageSize) + '" data-sort-by="' + soy.$$escapeHtml(opt_data.sortBy) + '" data-reverse-sort="' + soy.$$escapeHtml(opt_data.reverseSort) + '" data-blueprint-present="' + soy.$$escapeHtml(opt_data.blueprintPresent) + '"' + ((opt_data.analyticsKey) ? ' data-analytics-key="' + soy.$$escapeHtml(opt_data.analyticsKey) + '"' : '') + '>' + ((! opt_data.renderedHeadings) ? '<caption>' + soy.$$escapeHtml('Seiteneigenschaftenbericht wird erstellt...') + '</caption>' : '');
  if (opt_data.blueprintPresent) {
    output += Confluence.Templates.Macro.MasterDetail.detailsSummaryBlueprintBlankExperience(opt_data);
  } else {
    output += '<thead>' + ((opt_data.renderedHeadings) ? Confluence.Templates.Macro.MasterDetail.detailsSummaryHeader(opt_data) : '') + '</thead><tbody>';
    if (opt_data.details) {
      var lineList62 = opt_data.details;
      var lineListLen62 = lineList62.length;
      if (lineListLen62 > 0) {
        for (var lineIndex62 = 0; lineIndex62 < lineListLen62; lineIndex62++) {
          var lineData62 = lineList62[lineIndex62];
          output += Confluence.Templates.Macro.MasterDetail.detailsSummaryLine({line: lineData62, showCommentsCount: opt_data.showCommentsCount, showLikesCount: opt_data.showLikesCount});
        }
      } else {
        output += Confluence.Templates.Macro.MasterDetail.detailsSummaryNoResults({headingCount: opt_data.renderedHeadings.length});
      }
    }
    output += '</tbody>';
  }
  output += '</table></div>';
  return output;
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.detailsSummary.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.detailsSummary';
}


Confluence.Templates.Macro.MasterDetail.detailsSummaryNoResults = function(opt_data, opt_ignored) {
  return '<tr><td colspan="' + soy.$$escapeHtml(opt_data.headingCount + 1) + '">' + soy.$$escapeHtml('Kein Inhalt gefunden.') + '</td></tr>';
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.detailsSummaryNoResults.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.detailsSummaryNoResults';
}


Confluence.Templates.Macro.MasterDetail.detailsSummaryHeader = function(opt_data, opt_ignored) {
  var output = '<tr><th>' + soy.$$escapeHtml(opt_data.firstColumnHeading) + '</th>';
  var headingList83 = opt_data.renderedHeadings;
  var headingListLen83 = headingList83.length;
  for (var headingIndex83 = 0; headingIndex83 < headingListLen83; headingIndex83++) {
    var headingData83 = headingList83[headingIndex83];
    output += '<th>' + soy.$$filterNoAutoescape(headingData83) + '</th>';
  }
  output += '</tr>';
  return output;
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.detailsSummaryHeader.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.detailsSummaryHeader';
}


Confluence.Templates.Macro.MasterDetail.detailsSummaryLine = function(opt_data, opt_ignored) {
  var output = '<tr><td class="title" data-content-id="' + soy.$$escapeHtml(opt_data.line.id) + '">' + Confluence.Templates.Macro.MasterDetail.contentLink2(opt_data) + '</td>';
  var lineDetailList97 = opt_data.line.details;
  var lineDetailListLen97 = lineDetailList97.length;
  for (var lineDetailIndex97 = 0; lineDetailIndex97 < lineDetailListLen97; lineDetailIndex97++) {
    var lineDetailData97 = lineDetailList97[lineDetailIndex97];
    output += '<td>' + soy.$$filterNoAutoescape(lineDetailData97 ? lineDetailData97 : '&nbsp;') + '</td>';
  }
  output += ((opt_data.showCommentsCount || opt_data.showLikesCount) ? '<td class="social">' + ((opt_data.showCommentsCount && opt_data.line.commentsCount > 0) ? '<span class="icon icon-comment">' + soy.$$escapeHtml('Kommentar:') + '</span> <span class="count">' + soy.$$escapeHtml(opt_data.line.commentsCount) + '</span>' : '') + ((opt_data.showLikesCount && opt_data.line.likesCount > 0) ? '<span class="icon icon-like">' + soy.$$escapeHtml('Gef\xe4llt mir:') + '</span> <span class="count">' + soy.$$escapeHtml(opt_data.line.likesCount) + '</span>' : '') + '</td>' : '') + '</tr>';
  return output;
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.detailsSummaryLine.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.detailsSummaryLine';
}


Confluence.Templates.Macro.MasterDetail.contentLink2 = function(opt_data, opt_ignored) {
  return '<a href="' + soy.$$escapeHtml("") + soy.$$escapeHtml(opt_data.line.relativeLink) + '">' + soy.$$escapeHtml(opt_data.line.title) + '</a>' + ((opt_data.line.subTitle) ? '&gt; <a href="' + soy.$$escapeHtml("") + soy.$$escapeHtml(opt_data.line.subRelativeLink) + '">' + soy.$$escapeHtml(opt_data.line.subTitle) + '</a>' : '');
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.contentLink2.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.contentLink2';
}


Confluence.Templates.Macro.MasterDetail.detailsSummaryBlueprintBlankExperience = function(opt_data, opt_ignored) {
  return '<div class="blueprint-blank-experience"><div class="content"><h2>' + soy.$$escapeHtml(opt_data.blankTitle) + '</h2><p>' + soy.$$escapeHtml(opt_data.blankDescription) + '</p></div><p>' + soy.$$filterNoAutoescape(opt_data.createFromTemplateHtml) + '</p></div>';
};
if (goog.DEBUG) {
  Confluence.Templates.Macro.MasterDetail.detailsSummaryBlueprintBlankExperience.soyTemplateName = 'Confluence.Templates.Macro.MasterDetail.detailsSummaryBlueprintBlankExperience';
}

}catch(e){WRMCB(e)};
;
try {
/* module-key = 'confluence.extra.masterdetail:master-details-resources', location = 'com/atlassian/confluence/extra/masterdetail/js/metadata-summary-analytics.js' */
AJS.$(function(a){a(".metadata-summary-macro[data-analytics-key]").on("click",".title a",function(d){var b=a(d.delegateTarget).data("analytics-key");var c="metadata-summary-macro.content-click."+b;var e=a(this).parent().data("content-id");AJS.trigger("analytics",{name:c,data:{contentId:e}})})});
}catch(e){WRMCB(e)};
;
try {
/* module-key = 'confluence.extra.masterdetail:master-details-resources', location = 'com/atlassian/confluence/extra/masterdetail/js/metadata-summary.js' */
AJS.$(function(b){var a=function(c){if(c){return(""+c).replace(/[\[\]]/g,"")}return""};b(".metadata-summary-macro").each(function(){var l=b(this);var r=l.parent(),t=l.data("count-comments"),s=l.data("count-likes"),x=l.data("page-size"),y=l.data("total-pages"),c=l.data("headings"),f=l.data("first-column-heading"),p=a(l.data("sort-by")),n=a(l.data("reverse-sort")),v=l.data("blueprint-present"),j=a(l.data("spaces"));function i(z,B){var A=Confluence.Templates.Macro.MasterDetail.detailsSummaryLine({line:z,showCommentsCount:t,showLikesCount:s});B.append(A);l.trigger("update")}function k(z){return Confluence.Templates.Macro.MasterDetail.detailsSummaryLine({line:z,showCommentsCount:t,showLikesCount:s})}function o(B,A){var z=Confluence.Templates.Macro.MasterDetail.detailsSummaryHeader({firstColumnHeading:f,renderedHeadings:B});A.append(z)}function g(B,A){var z=Confluence.Templates.Macro.MasterDetail.detailsSummaryNoResults({headingCount:B.length});A.append(z)}var e={scope:r,pageSize:x,totalPages:y,path:"/rest/masterdetail/1.0/detailssummary/lines",data:{cql:l.data("cql"),spaceKey:AJS.Meta.get("space-key"),contentId:AJS.Meta.get("page-id"),detailsId:l.data("details-id"),headings:c,countComments:t,countLikes:s,sortBy:p,reverseSort:n},success:i};var w=l.find("caption");function h(B){w.hide();if(B.detailLines.length===0&&v){b(r).find("div.blueprint-blank-experience").show()}else{r.hide();o(B.renderedHeadings,l.find("thead"));var z=l.find("tbody");if(B.detailLines.length===0){g(B.renderedHeadings,z)}else{AJS.tablessortable.setTableSortable(l);var A="";_.each(B.detailLines,function(C){A+=k(C)});z.append(A);l.trigger("update")}if(B.totalPages>1){e.totalPages=B.totalPages;Confluence.UI.Components.Pagination.build(e)}r.slideDown()}}function m(z,B,A){w.text("Fehler beim Abrufen der Daten zum Seiteneigenschaftsbericht"+": "+A)}var u=b.extend({},{pageSize:x,pageIndex:0},e.data);var q=AJS.contextPath()+e.path;var d=l.children("thead").children("tr").length!==0;if(d){if(y>1){Confluence.UI.Components.Pagination.build(e)}}else{b.getJSON(q,u).done(h).fail(m)}})});
}catch(e){WRMCB(e)};