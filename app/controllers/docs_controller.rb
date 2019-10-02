# frozen_string_literal: true

class DocsController < ApplicationController
  before_action :find_pages
  def index; end

  generate_all :doc
  def show
    @doc = Doc.find(id: params.id)
    @aside = "docs/_aside_index"
  end

  private

  def find_pages
    @pages = Doc.all.sort_by(&:priority)
  end
end
