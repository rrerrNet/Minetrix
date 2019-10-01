# frozen_string_literal: true

class ChangelogController < ApplicationController
  def index
    @changelog = `git show origin/master:CHANGELOG.md`
  end
end
