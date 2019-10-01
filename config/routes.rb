# frozen_string_literal: true

App.draw_routes do
  # This defines the landing page of your website.
  root "home#index"

  # Additional routes can be created by using `map`.
  # For example to map `/projects` to the index method of ProjectsController,
  # you can do this:
  #
  # map "/projects" => "projects#index"
end
