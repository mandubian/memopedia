// An example Backbone application contributed by
// [JÃ©rÃ´me Gravel-Niquet](http://jgn.me/). This demo uses a simple
// [LocalStorage adapter](backbone-localstorage.js)
// to persist Backbone models within your browser.

// Load the application once the DOM is ready, using `jQuery.ready`:
$(function () {

    var template = function (id, data) {
        var template = $(id)
        var str = template.text()
        return Mustache.to_html(str, data)
    }

    var CardView = Backbone.View.extend({

        //... is a list tag.
        //tagName:"section",

        // Cache the template function for a single item.
        //template: _.template($('#item-template').html()),

        // The DOM events specific to an item.
        events:{
            "click nav":"nextEvent"
        },

        initialize:function () {
            console.log("CardView init")
        },

        // Re-render the titles of the todo item.
        render:function () {
            var card = template('#card', {word:'hello', trad:'toto', img:{url:'http://4.bp.blogspot.com/-RyF6qje3RFE/T1ux5UdiENI/AAAAAAAAAkI/uFCHTdqlzA0/s1600/learn-all-the-things.jpg'}, def:'un mot', sound:'paf'});
            this.$el.html(card);
            return this;
        },

        nextEvent:function () {
            console.log("nextEvent")
            this.options.next();
        }

    });
    // The Application
    // ---------------

    // Our overall **AppView** is the top-level piece of UI.
    var AppView = Backbone.View.extend({

        // Instead of generating a new element, bind to the existing skeleton of
        // the App already present in the HTML.
        el:$("#content"),

        // Our template for the line of statistics at the bottom of the app.
        //statsTemplate: _.template($('#stats-template').html()),

        // Delegated events for creating new items, and clearing completed ones.
        events:{
            //"keypress #new-todo":  "createOnEnter",
            //"click #clear-completed": "clearCompleted",
            //"click #toggle-all": "toggleAllComplete"
        },

        // At initialization we bind to the relevant events on the `Todos`
        // collection, when items are added or changed. Kick things off by
        // loading any preexisting todos that might be saved in *localStorage*.
        initialize:function () {
            console.log("init")
            var next = _.bind(this.next, this);
            var cardView = new CardView({next:next});
            this.currentView = cardView;
            this.$el.append(cardView.render().$el);
        },

        // Re-rendering the App just means refreshing the statistics -- the rest
        // of the app doesn't change.
        render:function () {
            console.log("render");
        },

        next:function () {
            var self = this;
            var next = _.bind(this.next, this);
            var cardView = new CardView({next:next});
            this.$el.append(cardView.render().$el.hide());
            this.currentView.$el.fadeOut(function () {
                cardView.$el.fadeIn(function () {
                    self.currentView.remove();
                    self.currentView = cardView;
                });
            });
        }

    });

    // Finally, we kick things off by creating the **App**.
    var App = new AppView;

});