const tabs = document.getElementById('tabs');
let visibleTable = document.getElementById('error-log');
tabs.addEventListener('click', (ev) => {
    const idToShow = ev.target.innerText.toLowerCase().replace(' ', '-');
    visibleTable.style.display = 'none';
    visibleTable = document.getElementById(idToShow);
    visibleTable.style.display = 'block';
});


document.addEventListener('DOMContentLoaded', () => {
    const lastTab = tabs.children[tabs.children.length - 1].children[0];
    visibleTable = document.getElementById(lastTab.innerText.toLowerCase().replace(' ', '-'));
    visibleTable.style.display = 'block';
}, false);