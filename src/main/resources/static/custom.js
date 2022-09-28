let map;
function initMap() {
	let userIcon = {url:'/images/userIcon.png', scaledSize: new google.maps.Size(150,150)}
	let busIcon = {url:'/images/bus.png', scaledSize: new google.maps.Size(50,50)}

    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: parseFloat(userLocation.lat), lng: parseFloat(userLocation.lng) },
        zoom: 15,
        scrollwheel: false
    });
    
    let userMarker= new google.maps.Marker({
    	position: { lat: parseFloat(userLocation.lat), lng: parseFloat(userLocation.lng) },
    	map: map,
    	icon: userIcon,
    	animation: google.maps.Animation.BOUNCE,
    	title: "YOU ARE HERE",
    });
    
    for (let i=0; i<busLocations.length; i++){
        let marker = new google.maps.Marker({
            position: { lat: parseFloat(busLocations[i].LATITUDE), lng: parseFloat(busLocations[i].LONGITUDE) },
            icon: busIcon,
            map: map,
        });
        
        let contentString = '<h4>' + 'Bus: ' + busLocations[i].VEHICLE + '</h4>';
        
        let infowindow = new google.maps.InfoWindow({ content: contentString});
        
        google.maps.event.addListener(marker, 'click', function() { infowindow.open(map,marker);}); 
       
    }   
   

}