function loadDataForGraph(internalName) {
    const url = `http://${window.location.host}/product-price/${internalName}`;
    console.log(`loading from ${url}`);
    return fetch(url,
        {
            method: 'GET',
            credentials: 'include'
        }
    ).then(
        response => response.json()
    );
}


function renderGraph(internalName) {
    loadDataForGraph(internalName)
        .then(
            response => {
                console.log(response);
                const trace = {
                    x: response.details.map(it => it.timestamp),
                    y: response.details.map(it => it.prize),
                    type: 'scatter'
                };
                const data = [trace];
                Plotly.newPlot('chart', data);
                console.log('chart loaded...');

                const average = arr => arr.reduce((p, c) => p + c, 0) / arr.length;
                const averagePrice = average(response.details.map(it => it.prize));

                const priceMgs = `The average price over all is ${averagePrice}`;
                const newElement = document.createElement('p');
                newElement.appendChild(document.createTextNode(priceMgs));

                document.getElementById('main-content')
                    .appendChild(newElement);
            }
        );
}