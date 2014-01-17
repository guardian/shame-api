require(['bonzo', 'common/$', 'common/utils/ajax'], function(bonzo, $, ajax){

    var config = {
            appRoot: "http://localhost:9000"
        },
        selectors = {
            list: '.right-most-popular__items',
            heading: '.right-most-popular__title',
            pageHeader: '#header'
        },
        els = function(){
            var tmp = {};
            for (var key in selectors) {
                if (selectors.hasOwnProperty(key)) {
                    tmp[key] = bonzo($(selectors[key]));
                }
            }
            tmp.body = bonzo(document.body);
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
                    title: shame.standfirst,
                    alt: shame.webTitle,
                    thumbnail: shame.image
                };
            els.list.append(template(shameItemTemplate, replacements));
        },
        includeStyles= function() {
            var styles = document.createElement('link');
            styles.setAttribute('href', config.appRoot + '/assets/stylesheets/sidebar-of-shame.css');
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
                    url: config.appRoot + "/",
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
        mailifyRHCol = function() {
            els.heading.text("DON'T MISS");
            els.body.addClass("shameful");
        },
        mailify = function() {
            var headerTemplate = "<div id='mail-header'><img src='{root}/assets/images/articleMockHeader.png' /></div>";
            els.body.addClass("very-shameful");
            bonzo(bonzo.create(template(headerTemplate, {root: config.appRoot}))).insertBefore(els.pageHeader);
        },
        action = function(currentState) {
            if (0 === currentState) {
                start();
            } if (1 === currentState) {
                mailifyRHCol();
            } if (2 === currentState) {
                mailify();
            }
        };

    document.addEventListener("keypress", events.keypress, false);
});
