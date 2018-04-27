function highlightActiveCategory() {
    let currentUrl = window.location.href;
    let links = document.querySelectorAll(".sidebar-sticky .nav-link");
    if (!currentUrl.includes("category")) {
        console.log("All categories");
        links[0].classList.add("active");
    } else {
        let index = currentUrl.lastIndexOf("/");
        let categoryName = currentUrl.substr(index + 1, currentUrl.length - index);
        console.log(`Category: ${categoryName}`);
        for (let i = 0; i < links.length; i++) {
            let link = links[i];
            if (link.href.includes(categoryName)) {
                link.classList.add("active");
                break;
            }
        }
    }
    console.log('highlight ready');
}

document.addEventListener('DOMContentLoaded', highlightActiveCategory, false);
