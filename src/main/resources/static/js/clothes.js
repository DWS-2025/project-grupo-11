document.getElementById('load-more').addEventListener('click', function () {
  const button = this;
  const spinner = document.getElementById('spinner');
  const productList = document.getElementById('product-list');
  const page = parseInt(button.getAttribute('data-page'));

  button.style.display = 'none';
  spinner.style.display = 'block';

  fetch(`/clothes/more/?page=${page}`)
    .then(response => response.json())
    .then(products => {
      products.forEach(product => {
        const productHTML = `
          <div class="feat_prod_box">
            <div class="prod_img">
              <img src="/product-image/${product.id}/" alt="${product.name}" class="product-image" />
            </div>
            <div class="prod_det_box">
              <div class="prod_title">${product.name}</div>
              <p class="details">${product.description}</p>
              <p class="price">Precio: ${product.price}€</p>
              <button onclick="window.location.href='/view/${product.id}/'">Más información</button>
            </div>
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
