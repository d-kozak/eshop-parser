const parseProductForm = document.getElementById('parse-product-form');
const parseCategoryForm = document.getElementById('parse-category-form');
const parseAllForm = document.getElementById('parse-all-form');
const tableBody = document.getElementById('table-body');

const csrfInput = document.getElementById('csrf');

parseAllForm.addEventListener('submit',
    (e) => {
        e.preventDefault();
        parseAll();
    }
);

parseCategoryForm.addEventListener('submit', e => {
    e.preventDefault();
    parseCategory(document.getElementById('productCategoryUrl').value);
});

parseProductForm.addEventListener('submit', e => {
    e.preventDefault();
    parseProduct(document.getElementById('productUrl').value);
});

function parseProduct(url) {
    if (!url) {
        alert('Please fill in product url first');
        return;
    }

    fetch('/parse/product/', {
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json',
            'X-CSRF-TOKEN': csrfInput.value
        },
        method: 'POST',
        credentials: 'include',
        body: url
    }).then(response => {
        if (response.status == 200) {
            return response.json();

        } else {
            alert('Failed ' + response.status);
            console.log('Failed', response.status);
            throw `Bad status code ${response.status}`;
        }
    }).then(
        response => {
            console.log('Loaded', response);
            alert('Loaded product ' + response.externalName);
        }
    ).catch(
        reason => {
            console.log(reason);
            alert(reason);
        }
    );
}

function parseCategory(url) {
    if (!url) {
        alert('Please fill in category url first');
        return;
    }

    fetch('/parse/category/', {
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json',
            'X-CSRF-TOKEN': csrfInput.valuera
        },
        method: 'POST',
        credentials: 'include',
        body: url
    }).then(
        response => {
            if (response.status == 200) {
                console.log("Started");
                $('#logs-dialog').modal();
                setTimeout(loadLogs, 2000);
            } else {
                alert('Failed ' + response.status);
                console.log('Failed', response.status);
            }
        }
    ).catch(
        reason => {
            console.log(reason);
            alert(reason);
        }
    );
}

function parseAll() {
    fetch('/parse/all', {
        method: 'GET',
        credentials: 'include'
    }).then(
        response => {
            if (response.status == 200) {
                console.log("Started");
                $('#logs-dialog').modal();
                setTimeout(loadLogs, 2000);
            } else {
                alert('Failed ' + response.status);
                console.log('Failed', response.status);
            }
        }
    ).catch(
        reason => {
            console.log(reason);
            alert(reason);
        }
    );
}


function loadLogs() {
    fetch('/logs', {
        method: 'GET',
        credentials: 'include'
    }).then(
        response => response.json()
    ).then(
        response => {
            console.log('Loaded: ', response);
            response.map(log => `<td>${log.url}</td><td>${log.state}</td>`)
                .map(line => {
                    const tableRow = document.createElement('tr');
                    tableRow.innerHTML = line;
                    return tableRow;
                }).forEach(tableRow => tableBody.appendChild(tableRow));
            setTimeout(loadLogs, 2000);
        }
    ).catch(reason => console.log(reason));

}