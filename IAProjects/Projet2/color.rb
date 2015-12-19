color_ids = {" " =>  0,
             "#" =>  0,
             "@" => 33,
             "X" => 32,
             "." => 34,
             "$" => 31}

$stdin.each_line do |line|
  line.chomp!
  line.each_char do |c|
    color_ids[c] ||= 30 + color_ids.size
  end

  puts line.gsub(/(.)/) { "\e[#{color_ids[$1]}m#$1\e[0m" }
end
