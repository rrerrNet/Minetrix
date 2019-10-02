# frozen_string_literal: true

App.draw_routes do
  # This defines the landing page of your website.
  root "home#index"

  map "/changelog" => "changelog#index"

  resources :docs, path: "docs"
end
