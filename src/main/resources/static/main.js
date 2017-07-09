function lookupPages() {
  $.ajax({
    type: 'GET',
    url: '/app/pages',
    dataType: 'json',
    success: renderPages
  });
}

function renderPages(json) {
  $("#pageList").empty()
  $.each(json.pages, function(idx, page) {
    $("#pageList").append(`
      <div class="page">
        <div class="row">
          <div class="col-sm-6">${page.name}</div>
          <div class="col-sm-6">
            <div class="btn-group" role="group">
              <button type="button" class="btn btn-default">On</button>
              <button type="button" class="btn btn-default">Off</button>
            </div>
          </div>
        </div>
      </div>
    `);
  });
}

