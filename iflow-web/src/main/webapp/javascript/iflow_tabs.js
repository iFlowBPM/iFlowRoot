function showActionMenu(anchorId, menuId) {
	document.getElementById(menuId).style.top = getElementY(document.getElementById(anchorId)) + getElementHeight(document.getElementById(anchorId));
	document.getElementById(menuId).style.left = getElementX(document.getElementById(anchorId));
	document.getElementById(menuId).style.visibility = "visible";
}

function hideActionMenu(anchorId, menuId) {
	document.getElementById(menuId).style.visibility = "hidden";
}

function toggleObj(tableId) {
	var vista = (document.getElementById(tableId).style.display == 'none') ? 'block' : 'none';
	document.getElementById('adminiflow').style.display = 'none';
	document.getElementById('adminflows').style.display = 'none';
	document.getElementById('adminprocs').style.display = 'none';
	document.getElementById('adminusers').style.display = 'none';
	document.getElementById(tableId).style.display = vista;
}

function getElementY(element) {
	var targetTop = 0;
	if (element.offsetParent) {
		while (element.offsetParent) {
			targetTop += element.offsetTop;
			element = element.offsetParent;
		}
	} else if (element.y) {
		targetTop += element.y;
	}
	return targetTop;
}

function getElementX(element) {
	var targetLeft = 0;
	if (element.offsetParent) {
		while (element.offsetParent) {
			targetLeft += element.offsetLeft;
			element = element.offsetParent;
		}
	} else if (element.x) {
		targetLeft += element.x;
	}
	return targetLeft;
}

function getElementWidth(element) {
	var targetWidth = 0;
	if (element.offsetParent) {
		targetWidth += element.offsetWidth;
	} else if (element.width) {
		targetWidth += element.width;
	}
	return targetWidth;
}

function getElementHeight(element) {
	var targetHeight = 0;
	if (element.offsetParent) {
		targetHeight += element.offsetHeight;
	} else if (element.height) {
		targetHeight += element.height;
	}
	return targetHeight;
}
