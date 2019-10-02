# frozen_string_literal: true

require "erb"

class Doc < FoxPage::Model[:dir]
  def url
    App.config.site.base_path + "/docs/#{id}"
  end

  def parsed_content
    ERB.new(content).result(binding)
  end

  private

  def default_config
    `git show origin/master:src/main/resources/config.yml`.chomp
  end
end
