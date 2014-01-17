require(['bonzo', 'common/$', 'common/utils/ajax'], function(bonzo, $, ajax){

    var selectors = {
            list: '.right-most-popular__items',
            heading: '.right-most-popular__title'
        },
        els = function(){
            var tmp = {};
            for (var key in selectors) {
                if (selectors.hasOwnProperty(key)) {
                    tmp[key] = bonzo($(selectors[key]));
                }
            }
            return tmp;
        }(),
        state = 0,
        events = {
            keypress: function(e) {
                if (115 === e.charCode) {  // listen for 's'
                    action(state);
                    state++;
                }
            }
        },
        template = function(format, replacements) {
            return format.replace(/\{(.+?)\}/g, function(_, key){
                return replacements[key];
            });
        },
        addShame = function(shame) {
            var shameItemTemplate = '<li class="right-most-popular-item" data-link-name="trail"><a class="right-most-popular-item__url media u-cf" href="{url}"><div class="right-most-popular-item__img media__img js-image-upgrade" data-src="{thumbnail}"><img class="responsive-img" src="{thumbnail}" alt="{alt}"></div><h3 class="right-most-popular-item__headline media__body">{title}</h3></a></li>',
                replacements = {
                    url: shame.webUrl,
                    title: shame.webTitle,
                    alt: shame.webTitle,
                    thumbnail: shame.image
                };
            els.list.append(template(shameItemTemplate, replacements));
        },
        includeStyles= function() {
            var styles = document.createElement('link');
            styles.setAttribute('href','http://localhost:9000/assets/stylesheets/sidebar-of-shame.css');
            styles.setAttribute('type','text/css');
            styles.setAttribute('rel','stylesheet');
            document.head.appendChild(styles);
        },
        makeShameful = function(shame) {
            for (var i=0,l=shame.length; i<l; i++) {
                addShame(shame[i]);
            }
        },
        pageIsEligible = function() {
            return "Article" == guardian.config.page.contentType;
        },
        start = function() {
            if (pageIsEligible()) {
                els.list.empty();
                includeStyles();
                var updating = bonzo(bonzo.create("<li><div class='is-updating'>Loading...</div></li>"));
                updating.appendTo(els.list);
                ajax({
                    url: "http://localhost:9000/",
                    type: "jsonp",
                    crossOrigin: true
                }).then(
                    function(response) {
                        updating.remove();
                        makeShameful(response);
                    }
                );
            }
        },
        action = function(currentState){
            var body = bonzo(document.body);
            if (0 === currentState) {
                start();
            } if (1 === currentState) {
                els.heading.text("DON'T MISS");
                body.addClass("shameful");
            } if (2 === currentState) {
                body.addClass("very-shameful");
            }
        };

    document.addEventListener("keypress", events.keypress, false);
});
