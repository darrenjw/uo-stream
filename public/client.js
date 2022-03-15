
const map = new ol.Map({
    target: 'map',
    view: new ol.View({
        center: ol.proj.fromLonLat([-1.6165137,54.97271]),
        zoom: 12,
        minZoom: 12,
        maxZoom: 12,
    }),
    controls: []
});

const tileLayer = new ol.layer.Tile({
    source: new ol.source.OSM({
        url: 'http://a.tile.stamen.com/toner/{z}/{x}/{y}.png'
    })
});

const imageLayer = new ol.layer.Image({
    source: new ol.source.ImageCanvas({
        canvasFunction: function (extent, resolution, pixelRatio, size, projection) {
            const canvas = document.createElement('canvas');
            const canvasWidth = size[0], canvasHeight = size[1];
            canvas.setAttribute('width', canvasWidth);
            canvas.setAttribute('height', canvasHeight);

            return canvas;
        },
        projection: 'EPSG:3857'
    })
});

map.addLayer(tileLayer);
map.addLayer(imageLayer);


export const renderData = (data) => {
    setTimeout(() => {
        imageLayer.getSource().changed();
    }, 0)

    setTimeout(() => {
        console.info('Render begin')
        const canvas = document.getElementsByTagName('canvas')[0];
        const context = canvas.getContext('2d');

        data.locations.forEach(location => {
            const {lng, lat, colour} = location;
            const point =  new ol.geom.Point(ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857'));
            const pixel = map.getPixelFromCoordinate(point.flatCoordinates);
            const cX = pixel[0], cY = pixel[1];
            context.fillStyle = `rgba(${colour.red}, ${colour.green}, ${colour.blue}, ${colour.alpha})`;
            context.fillRect(cX, cY, 9, 11);
        });
        console.info('Render end')
    }, 1000);

};


