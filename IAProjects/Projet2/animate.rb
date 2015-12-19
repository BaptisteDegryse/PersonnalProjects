WaitTime = 0.2

def clear_screen
  print "\e[2J\e[1;1H"
end

clear_screen

$stdin.each_line do |line|
  if line.chomp.empty?
    sleep(WaitTime)
    clear_screen
  else
    puts line
  end
end
