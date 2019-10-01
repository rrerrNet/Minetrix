# frozen_string_literal: true

class ChangelogController < ApplicationController
  def index
    @changelog = `git show origin/master:CHANGELOG.md`.sub(/\A.+adheres\s*to.*\.$/m, "")
  end
end
