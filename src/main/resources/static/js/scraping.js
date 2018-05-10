const parseProductForm = document.getElementById('parse-product-form');
const parseCategoryForm = document.getElementById('parse-category-form');
const parseAllForm = document.getElementById('parse-all-form');

const tableBody = document.getElementById('table-body');

parseAllForm.addEventListener('submit',
    (e) => {
        fetch('/parse/all', {
            method: 'GET',
            credentials: 'include'
        }).then(
            response => {
                console.log("Started");
                $('#logs-dialog').modal();
                setTimeout(loadLogs, 2000);
            }
        ).catch(
            reason => {
                console.log(reason);
            }
        );
        e.preventDefault();
    }
);


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