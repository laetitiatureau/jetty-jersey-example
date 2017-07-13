Vue.use(VueResource)

var app = new Vue({
    el: '#app',
    data: {
        pages: []
    },
    methods: {
        activatePage(name) {
            this.$http.put('http://localhost:8080/app/pages/' + name)
                .then(function(response) {
                    this.updatePage(name, response.body.active)
                })
        },
        deactivatePage(name) {
            this.$http.delete('http://localhost:8080/app/pages/' + name)
                .then(function(response) {
                    this.updatePage(name, response.body.active)
                })
        },
        updatePage(name, active) {
            for (var i = 0; i < this.pages.length; i++) {
                var page = this.pages[i];
                if (page.name == name) {
                    page.active = active
                }
            }
        },
        fetchPages() {
            this.$http.get('http://localhost:8080/app/pages')
                .then(function(response) {
                    this.pages = response.body.pages
                })
         }
    },
    created: function() {
        this.fetchPages()
    }
})
