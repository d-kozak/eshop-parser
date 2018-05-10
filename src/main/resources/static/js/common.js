const find = () => {
    const query = document.getElementById("search-input").value;
    window.location.href = `/?search=${query}`;
};