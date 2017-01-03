app.controller('OCRController', ['$scope', '$location',
	function($scope, $location){
		$scope.changeView=function(view){
			$location.path(view);
		}
		

	}

]);