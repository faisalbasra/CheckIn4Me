#*****************************************************************************
#    This file is part of CheckIn4Me.  Copyright © 2010  David Ivins
#
#    CheckIn4Me is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    CheckIn4Me is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with CheckIn4Me.  If not, see <http://www.gnu.org/licenses/>.
#*****************************************************************************
#!/usr/bin/perl -w
use strict; 

my $dir = "/Users/david/Documents/Projects/Repositories/checkin4me/CheckIn4me";

if (-e "$dir/AndroidManifest.xml")
{
	system("rm $dir/AndroidManifest.xml");
}

system("ln -s $dir/FreeManifest.xml $dir/AndroidManifest.xml");