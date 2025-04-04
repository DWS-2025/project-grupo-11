document.getElementById('load-more').addEventListener('click', function () {
  const button = this;
  const spinner = document.getElementById('spinner');
  const productList = document.getElementById('product-list');
  const page = parseInt(button.getAttribute('data-page')) || 1;

  const urlParams = new URLSearchParams(window.location.search);
  const name = urlParams.get('name') || '';
  const description = urlParams.get('description') || '';
  const price = urlParams.get('price') || '';

  button.style.display = 'none';
  spinner.style.display = 'block';

  let url = `/clothes/more/?page=${page}`;
  if (name || description || price) {
    url = `/search-products/more/?page=${page}&name=${encodeURIComponent(name)}&description=${encodeURIComponent(description)}&price=${encodeURIComponent(price)}`;
  }

  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(products => {
      if (products.length === 0) {
        button.style.display = 'none';
        spinner.style.display = 'none';
        return;
      }

      products.forEach(product => {
        const productHTML = `
          <div class="feat_prod_box">
            <div class="prod_img">
              <img src="/product-image/${product.id}/" alt="${product.name}" border="0" class="product-image" />
            </div>
            <div class="prod_det_box">
              <div class="box_top"></div>
              <div class="box_center">
                <div class="prod_title">${product.name}</div>
                <p class="details">${product.description}</p>
                <p class="price">Precio: ${product.price}€</p>
                <button onclick="window.location.href='/view/${product.id}/'">Más información</button>
                <div class="clear"></div>
              </div>
              <div class="box_bottom"></div>
            </div>
            <div class="clear"></div>
          </div>
        `;
        productList.insertAdjacentHTML('beforeend', productHTML);
      });

      button.setAttribute('data-page', page + 1);
      button.style.display = 'block';
      spinner.style.display = 'none';
    })
    .catch(error => {
      console.error('Error al cargar más productos:', error);
      spinner.style.display = 'none';
      button.style.display = 'block';
    });
});