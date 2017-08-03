using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Diffusion_Test
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            update_text();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            setup_grid();
        }

        

        const int cell_size = 50;

        private List<ChunkPollution> list = new List<ChunkPollution>();

        int im = 0;
        int jm = 0;

        private void setup_grid()
        {
            list.Clear();
            sources.Clear();
            panel1.Controls.Clear();

            GC.Collect();

            panel1.SetBounds(panel1.Location.X, panel1.Location.Y, this.Width - 20, this.Height - 100);

            int sizeX = this.panel1.Size.Width;
            int sizeZ = this.panel1.Size.Height;

            this.panel1.SuspendLayout();
            this.SuspendLayout();

            im = sizeX / cell_size;
            jm = sizeZ / cell_size;

            for (int i = 0; i < im; i++)
                for (int j = 0; j < jm; j++)
                {
                    list.Add(new ChunkPollution(i, j, 0));

                    Label l = new Label();

                    l.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
                    l.Font = new System.Drawing.Font("Times New Roman", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
                    l.Location = new System.Drawing.Point(i * cell_size, j * cell_size);
                    l.Name = "cell"+i+"_"+j;
                    l.Size = new System.Drawing.Size(cell_size, cell_size);
                    l.Text = "X";
                    l.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;

                    l.MouseClick += new System.Windows.Forms.MouseEventHandler(this.cell_click);

                    this.panel1.Controls.Add(l);
                }

            this.panel1.ResumeLayout(false);
            this.ResumeLayout(false);

            update_text();
        }

        private void reset_button_Click(object sender, EventArgs e)
        {
            setup_grid();

            if (timer1.Enabled)
                timer1.Enabled = false;

            timer_toggle.Text = "Start Timer";
        }

        private void update_text()
        {
            for (int i = 0; i < im; i++)
                for (int j = 0; j < jm; j++)
                {
                    Label l = (Label)panel1.Controls.Find("cell" + i + "_" + j, false)[0];

                    l.Text = ""+getPollution(i, j);
                }
        }

        private void update_color()
        {
            for (int i = 0; i < im; i++)
                for (int j = 0; j < jm; j++)
                {
                    Label l = (Label)panel1.Controls.Find("cell" + i + "_" + j, false)[0];

                    if (hasListChunkPollution(sources, i, j))
                    {
                        l.BackColor = Color.Red;
                        continue;
                    }

                    double coef = (double)(getPollution(i, j) * 2550) / (double)addition_meter.Maximum;

                    if ((int)coef > 255)
                        coef = 255;

                    l.BackColor = Color.FromArgb(200, (int)(255 - (int)coef), 255, (255 - (int)coef));
                }
        }

        private double getPollution(int i, int j)
        {
            if (i < 0 || i > im || j < 0 || j > jm)
            {
                return -1;
            }

            foreach (ChunkPollution item in list)
            {
                if (item.x == i && item.z == j)
                {
                    return item.pollution;
                }
            }

            return -1;
        }

        List<ChunkPollution> sources = new List<ChunkPollution>();

        private bool hasListChunkPollution(List<ChunkPollution> li, int x, int z)
        {
            if (x < 0 || x > im || z < 0 || z > jm)
            {
                return false;
            }

            foreach (ChunkPollution item in li)
            {
                if (item.x == x && item.z == z)
                {
                    return true;
                }
            }

            return false;
        }

        private void setPollution(int x, int z, double pollution)
        {
            if (x < 0 || x > im || z < 0 || z > jm)
            {
                return;
            }

            if(!hasListChunkPollution(sources, x, z))
            for (int i = 0; i < list.Count; i++)
            {
                ChunkPollution item = list[i];
                if (item.x == x && item.z == z)
                {
                    list.Remove(item);
                    list.Add(new ChunkPollution(x, z, pollution > 0 ? pollution : 0));
                }
            }
        }

        private void addPollution(int x, int z, double delta)
        {
            if (delta + getPollution(x, z) > (double)addition_meter.Maximum)
            {
                delta = (double)addition_meter.Maximum - getPollution(x, z);
            }

            setPollution(x, z, delta + getPollution(x, z));
        }

        private void setSource(int x, int z, double emission)
        {
            if (!hasListChunkPollution(sources, x, z))
            {
                setPollution(x, z, emission);
                sources.Add(new ChunkPollution(x, z, emission));
            }
        }


        private void cell_click(object sender, EventArgs e)
        {
            Label l = (Label)sender;

            String s = l.Name.Substring(4);

            String[] crds = s.Split('_');

            if (crds.Length != 2)
            {
                return;
            }

            int i = int.Parse(crds[0]);
            int j = int.Parse(crds[1]);

            if (((MouseEventArgs)e).Button != MouseButtons.Middle)
                addPollution(i, j, (double)addition_meter.Value);
            else
            {
                if (hasListChunkPollution(sources, i, j))
                {
                    for (int k = 0; k < sources.Count; k++)
                    {
                        ChunkPollution item = sources[k];
                        if (item.x == i && item.z == j)
                        {
                            sources.Remove(item);
                        }
                    }
                }
                else
                    setSource(i, j, (double)addition_meter.Value);
            }

            update_text();

            update_color();
        }

        float diffusion_factor = 0.01F;

        public void diffuse(ChunkPollution c)
        {
            diffuse(c.x, c.z);
        }

        private void diffuse(int i, int j)
        {
            double origin_pollution = getPollution(i, j);

            int to_spread = (int)(origin_pollution * diffusion_factor) + 1;

            addPollution(i + 1, j, to_spread);
            addPollution(i - 1, j, to_spread);
            addPollution(i, j - 1, to_spread);
            addPollution(i, j + 1, to_spread);

            addPollution(i, j, -4F * to_spread);
        }

        private void process_diffusion()
        {
            for (int i = 0; i < im; i++)
                for (int j = 0; j < jm; j++)
                {
                    diffuse(i, j);
                }
        }

        private void update()
        {
            diffusion_factor = (float)coefmter.Value;
            process_diffusion();

            update_text();
            update_color();

            if (timer1.Interval != timinter.Value)
                timer1.Interval = (int)timinter.Value;
        }

        private void update_full(object sender, EventArgs e)
        {
            update();
        }

        private void timer_tick(object sender, EventArgs e)
        {
            update();
        }

        private void timer_toggle_Click(object sender, EventArgs e)
        {
            timer1.Enabled = !timer1.Enabled;

            timer_toggle.Text = timer_toggle.Text == "Start Timer" ? "Stop Timer" : "Start Timer";
        }
    }

    public class ChunkPollution
    {
        public int x;
        public int z;

        public double pollution;

        public ChunkPollution()
        {

        }

        public ChunkPollution(int nx, int nz, double npoll)
        {
            x = nx;
            z = nz;
            pollution = npoll;
        }
    }
}
