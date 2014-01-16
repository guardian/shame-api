require(['bonzo', 'common/$', 'common/utils/ajax'], function(bonzo, $, ajax){

    var selectors = {
            list: '.right-most-popular__items'
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
                    thumbnail: shame.thumbnail
                };
            els.list.append(template(shameItemTemplate, replacements));
        },
        addStyles= function() {
            var styles = document.createElement('link');
            styles.setAttribute('href','http://localhost:9000/assets/stylesheets/sidebar-of-shame.css');
            styles.setAttribute('type','text/css');
            styles.setAttribute('rel','stylesheet');
            document.head.appendChild(styles);
        },
        makeShameful = function(shame) {
            els.list.empty();
            addStyles();
            for (var i=0,l=shame.length; i<l; i++) {
                addShame(shame[i]);
            }
        };

    ajax({
        url: "http://localhost:9000/",
        type: "jsonp",
        crossOrigin: true
    }).then(
        function(response) {
            makeShameful(response);
        }
    );
});
