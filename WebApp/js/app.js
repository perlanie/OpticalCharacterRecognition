var app = angular.module('OCRApp', ['ngRoute']);

app.config (function ($routeProvider){
	$routeProvider
  	.when('/',{
  	 	controller: 'OCRController',
    	templateUrl: 'views/convolution.html'
  	})
  	.when('/zoning',{
  	 	controller: 'OCRController',
    	templateUrl: 'views/zoning.html'
  	})
  	.when('/thinning',{
  	 	controller: 'OCRController',
    	templateUrl: 'views/thinning.html'
  	})
  	.otherwise({
  		redirectTo:'/'
  	});

});