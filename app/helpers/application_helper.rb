# frozen_string_literal: true

require "kramdown"
require "kramdown-parser-gfm"

module ApplicationHelper
  def markdown(text)
    Kramdown::Document.new(
      text,
      input: "GFM",
      hard_wrap: false
    ).to_html
  end
end
