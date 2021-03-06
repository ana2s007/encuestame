/*
 * Copyright 2013 encuestame
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/***
 *  @author juanpicado19D0Tgm@ilDOTcom
 *  @version 1.146
 *  @module SignUp
 *  @namespace Widgets
 *  @class RealNameValidator
 */
define( [
         "dojo/_base/declare",
         "dijit/_WidgetBase",
         "dijit/_TemplatedMixin",
         "dijit/_WidgetsInTemplateMixin",
         "me/core/main_widgets/EnmeMainLayoutWidget",
         "me/web/widget/validator/AbstractValidatorWidget",
         "me/core/enme",
         "dojo/text!me/web/widget/validator/templates/realNameValidator.html" ],
        function(
                declare,
                _WidgetBase,
                _TemplatedMixin,
                _WidgetsInTemplateMixin,
                main_widget,
                abstractValidatorWidget,
                _ENME,
                 template ) {
            return declare( [ _WidgetBase, _TemplatedMixin, main_widget, abstractValidatorWidget, _WidgetsInTemplateMixin ], {

          // Template string.
           templateString: template,

       /*
        * Set the focus on load.
        */
        focusDefault: true,

         /*
          * Place holder html5 text
          */
         placeholder: "Write your Real Name",

         /*
          *
          */
         postCreate: function() {
             this.inherited( arguments );
         },

        /**
         *
         */
        _validate: function( event ) {
            this.inputTextValue = this._input.value;
                this._loadService(
                 this.getServiceUrl(), {
                context: this.enviroment,
                real_name: this._input.value
            }, this.error );
        },

        /**
         *
         */
        getServiceUrl: function() {
            return "encuestame.service.publicService.validate.realName";
        },

        /**
         *
         */
         error: function( error ) {
            console.debug("error", error );
         }

    });
});
