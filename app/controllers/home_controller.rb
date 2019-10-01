# frozen_string_literal: true

class HomeController < ApplicationController
  before_action :set_build_time

  def index; end

  private

  def set_build_time
    @build_time = Time.now
  end
end
