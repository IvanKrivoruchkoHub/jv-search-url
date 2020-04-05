
function getDataForTable() {
    jQuery.getJSON("/allUrl", function (data) {
        createTable(data);
    })
}

function createTable (data) {
    var html = '<table class="table table-striped">';
    html += '<tr>';
    $.each(data[0], function(index){
        html += '<th>'+index+'</th>';
    });
    html += '</tr>';
    $.each(data, function(index, value){
        html += '<tr>';
        $.each(value, function(index2, value2){
            html += '<td>'+value2+'</td>';
        });
        html += '<tr>';
    });
    html += '</table>';
    $('#tableContainer').html(html);
}

var interval = setInterval(getDataForTable, 2000);
$(document).ready(function() {
    $("#start").click(function() {
        interval = setInterval(getDataForTable, 2000);

        $('#stopped').text(null);
        $("#tableContainer").html(null);

        var search = {};
        search["startUrl"] = $("#url").val();
        search["searchText"] = $("#search_text").val();
        search["maxCountUrls"] = $("#count_urls").val();
        search["threadCount"] = $("#count_threads").val();
        $.ajax({
            type: "POST",
            async: true,
            contentType: "application/json",
            url: "/starting",
            data: JSON.stringify(search),
            dataType: 'json',
            timeout: 100000,
            cache: false,
            error: errorInfo
        })
    });

    $("#stop").click(function() {
        clearInterval(interval);
        $('#stopped').text("Stopped");
    });
});

function errorInfo (data) {
    var spanStop = $('#stopped');
    if (spanStop.text() !== "Stopped") {
    alert(data["responseText"]);
    }
}
