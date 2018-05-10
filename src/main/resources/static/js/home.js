class HomeController {

    constructor() {
        console.log('home controller created')
    }

    search(input) {
        const href = window.location.href;
        const categoryString = '/category/';
        let url;
        let category = undefined;
        if (href.includes(categoryString)) {
            const firstPart = href.indexOf(categoryString) + categoryString.length;
            category = href.substr(firstPart, href.length - firstPart);
            if (category.indexOf('/') !== -1) {
                category = category.substr(0, category.indexOf('/'));
            }
            if (category.indexOf('?') !== -1) {
                category = category.substr(0, category.indexOf('?'));
            }
            console.log('category: ', category);
        }
        if (category) {
            url = `/category/${category}?search=${input}`;
        } else {
            url = `/?search=${input}`;
        }
        window.location.href = url;
    }
}

const homeController = new HomeController();


document.addEventListener("scroll", (e) => {
        const lastDiv = document.querySelector("#scroll-content > div:last-child");
        const lastDivOffset = lastDiv.offsetTop + lastDiv.clientHeight;
        const pageOffset = window.pageYOffset + window.innerHeight;
        if (pageOffset > lastDivOffset - 20) {
            console.log('reached bottom');
        }
    }
);


document.getElementById('search-input').addEventListener('keyup',
    (e) => {
        if (e.keyCode === 13) {
            homeController.search(e.target.value);
        }
    }
);
