// An example Backbone application contributed by
// [JÃ©rÃ´me Gravel-Niquet](http://jgn.me/). This demo uses a simple
// [LocalStorage adapter](backbone-localstorage.js)
// to persist Backbone models within your browser.

// Load the application once the DOM is ready, using `jQuery.ready`:

/*
$(function(){

  var onGame = function(room){
    console.warn("TODO onGame: %o", room)
  }

  // w: String
  sendAnswer = function(answer) {
    w.send(JSON.stringify({word: answer}));
  }

  // Called when somebody gave an answer
  var onScore = function(score) {
    console.warn("TODO onScore: %o", score)
  }

  var onCard = function(card) {
    console.warn("TODO onCard: %o", card)
  }

  // Called when the other player has quitted the room
  var onQuit = function() {
    console.warn("TODO onQuit")
  }

  var onLose = function(room){
    console.warn("TODO onLose: %o", room)
  }

  var onWin = function(room){
    console.warn("TODO onWin: %o", room)
  }

  var w = new WebSocket("ws://" + window.location.host + "/events");
  w.onmessage = function(e) {
    console.log('received %o', e);
    var m = JSON.parse(e.data);

    if(m.start) onGame(m.start)
    if(m.score) onScore(m.score)
    if(m.word) onCard(m)
    if(m.close) onQuit()
    if(m.win) onWin(m.win)
    if(m.lose) onLose(m.lose)
  };
})
*/

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
            var card = template('#card', this.model);
            this.$el.html(card);
            return this;
        },

        nextEvent:function (e) {
            e.preventDefault();
            e.stopPropagation();
            console.log(e);
            console.log("nextEvent card")
            this.options.next();
            return false;
        }

    });

    var Question = Backbone.View.extend({

        //... is a list tag.
        //tagName:"section",

        // Cache the template function for a single item.
        //template: _.template($('#item-template').html()),

        // The DOM events specific to an item.
        events:{
            "input input":"checkAnswer"
        },

        initialize:function () {
            console.log("Question View init")
            setTimeout(this.nextEvent, 1000 * 30);
        },

        // Re-render the titles of the todo item.
        render:function () {
            var card = template('#question', this.model);
            this.$el.html(card);
            return this;
        },

        checkAnswer: function(){
            this.$el.find('input').val()
        },

        nextEvent:function () {
            e.preventDefault();
            e.stopPropagation();
            console.log("nextEvent Question")
            this.options.next();
            return false;
        }

    });
    // The Application
    // ---------------

    // Our overall **AppView** is the top-level piece of UI.
    AppView = Backbone.View.extend({

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
            var cardView = new CardView({next:next, model:this.model[0]});
            this.modelIndex = 1;
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
            var card = this.model[this.modelIndex % this.model.length];
            var cardView;
            if (card.point > 5) {
                cardView = new Question({next:next, model:card});
            } else {
                card.point += 5;
                cardView = new CardView({next:next, model:card});
            }
            this.modelIndex++;
            this.$el.append(cardView.render().$el.hide());
            this.currentView.$el.fadeOut(function () {
            });
            cardView.$el.fadeIn(function () {
                self.currentView.remove();
                self.currentView = cardView;
            });
        }

    });


});