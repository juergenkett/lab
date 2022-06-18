// This file was automatically generated from jqltable-settings-panel.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace org.swift.confluence.table.jqltable.editor.
 */

if (typeof org == 'undefined') { var org = {}; }
if (typeof org.swift == 'undefined') { org.swift = {}; }
if (typeof org.swift.confluence == 'undefined') { org.swift.confluence = {}; }
if (typeof org.swift.confluence.table == 'undefined') { org.swift.confluence.table = {}; }
if (typeof org.swift.confluence.table.jqltable == 'undefined') { org.swift.confluence.table.jqltable = {}; }
if (typeof org.swift.confluence.table.jqltable.editor == 'undefined') { org.swift.confluence.table.jqltable.editor = {}; }


org.swift.confluence.table.jqltable.editor.jqltableSettingsPanel = function(opt_data, opt_ignored) {
  opt_data = opt_data || {};
  return '<div id="jql-settings-tabs" class="aui-tabs horizontal-tabs"><ul class="tabs-menu"><li class="menu-item active-tab"><a href="#data-source">Data source</a></li><li class="menu-item"><a href="#data-format">Data format</a></li><li class="menu-item" id="jql-data-settings"><a href="#data-settings">Data settings</a></li></ul><div class="tabs-pane active-pane" id="data-source"><div class="scrollable-tab-content"><fieldset>' + org.swift.confluence.table.editor.textElement({name: 'jiraApplinks', value: opt_data.jiraApplinks, label: 'Jira application links', topLabel: 'true', required: 'true', helpContent: 'Select the Jira instance to be queried. If you dont see your intended Jira instance, contact your Jira admin for assistance.'}) + org.swift.confluence.table.editor.textElement({name: 'jiraAdvanceSearch', value: opt_data.jiraAdvanceSearch, label: 'Jira advanced search', topLabel: 'true', required: 'true', helpContent: 'Enter a JQL expression to search for issues in the specified Jira instance.'}) + '</fieldset></div></div><!-- tabs-pane data-source close--><div class="tabs-pane" id="data-format"><div class="scrollable-tab-content"><fieldset><h3>Augmentation</h3>' + org.swift.confluence.table.editor.textElement({name: 'augments', value: opt_data.augments, label: 'Augments to data row values', topLabel: 'true', helpContent: 'Comma separated list of augments to the data row values, one for each column. Double quote values containing a comma. See the \x3ca href\x3d\x22https://bobswift.atlassian.net/wiki/x/ooCSAg\x22 target\x3d\x22_blank\x22\x3edocumentation\x3c/a\x3e.'}) + org.swift.confluence.table.editor.textElement({name: 'headingAugments', value: opt_data.headingAugments, label: 'Augments to heading row values', topLabel: 'true', helpContent: 'Comma separated list of augments to heading row values, one for each column.'}) + org.swift.confluence.table.editor.textElement({name: 'footingAugments', value: opt_data.footingAugments, label: 'Augments to footing row values', topLabel: 'true', helpContent: 'Comma separated list of augments to footing row values, one for each column.'}) + '</fieldset></div></div><!-- tabs-pane data-format close--><div class="tabs-pane" id="data-settings"><div class="scrollable-tab-content"><fieldset>' + org.swift.confluence.table.editor.textElement({name: 'columns', value: opt_data.columns, label: 'Columns to display', topLabel: 'true', helpContent: 'Select one or more Jira issue fields from the list to be displayed as columns. Use Augments to heading row values field to modify issue field column headings.'}) + org.swift.confluence.table.editor.textElement({name: 'rowsToDisplay', type: 'number', value: opt_data.rowsToDisplay, label: 'Rows to display', topLabel: 'true', helpContent: 'Specify the total number of Jira issues to be displayed in the table.'}) + org.swift.confluence.table.editor.customToggleElement({name: 'output', value1: 'html', value2: 'wiki', value: opt_data.output, label: 'Output format'}) + org.swift.confluence.table.editor.toggleElement({name: 'table', value: opt_data.table, label: 'Show result as table'}) + org.swift.confluence.table.editor.toggleElement({name: 'capitalize', value: opt_data.capitalize, label: 'Capitalize first character of generated headings'}) + org.swift.confluence.table.editor.toggleElement({name: 'showWiki', value: opt_data.showWiki, label: 'Show non-formatted version of generated wiki'}) + org.swift.confluence.table.editor.toggleElement({name: 'escape', value: opt_data.escape, label: 'Escape special characters in wiki'}) + '</fieldset></div></div><!-- tabs-pane data-settings close--></div>';
};
if (goog.DEBUG) {
  org.swift.confluence.table.jqltable.editor.jqltableSettingsPanel.soyTemplateName = 'org.swift.confluence.table.jqltable.editor.jqltableSettingsPanel';
}