class HomeController {

    constructor() {
        this.category = undefined;
        console.log('home controller created')
    }

    search(input) {
        let url;
        if (this.category) {
            url = `/category/${this.category}?search=${input}`;
        } else {
            url = `/?search=${input}`;
        }
        window.location.href = url;
    }

    highlightActiveCategory() {
        const currentUrl = window.location.href;
        const links = document.querySelectorAll(".sidebar-sticky .nav-link");
        if (!currentUrl.includes("category")) {
            console.log("All categories");
            links[0].classList.add("active");
        } else {
            const index = currentUrl.lastIndexOf("/");
            this.category = currentUrl.substr(index + 1, currentUrl.length - index);
            console.log(`Category: ${this.category}`);
            for (let i = 0; i < links.length; i++) {
                const link = links[i];
                if (link.href.includes(this.category)) {
                    link.classList.add("active");
                    break;
                }
            }
        }
    }
}

const homeController = new HomeController();

document.addEventListener('DOMContentLoaded',
    (e) => homeController.highlightActiveCategory(),
    false
);

document.getElementById('search-input').addEventListener('keyup',
    (e) => {
        if (e.keyCode === 13) {
            homeController.search(e.target.value);
        }
    }
);
